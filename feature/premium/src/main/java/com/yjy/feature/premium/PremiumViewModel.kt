package com.yjy.feature.premium

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.yjy.common.network.fold
import com.yjy.data.user.api.UserRepository
import com.yjy.feature.premium.mapper.toUiModel
import com.yjy.feature.premium.model.PremiumUiEvent
import com.yjy.feature.premium.model.ProductUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _isPurchasing = MutableStateFlow(false)
    val isPurchasing = _isPurchasing.asStateFlow()

    private val _uiEvent = MutableSharedFlow<PremiumUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val isPremium = userRepository.isPremium
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    private val billingClient = callbackFlow {
        val client = createBillingClient { billingResult, purchases ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    val purchase = purchases?.firstOrNull()
                    if (purchase?.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        consumePurchase(purchase.purchaseToken)
                    }
                }

                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    Timber.e("Purchase canceled by user")
                }

                else -> {
                    Timber.e("Purchase failed: ${billingResult.debugMessage}")
                    sendEvent(PremiumUiEvent.PurchaseFailure)
                }
            }
        }

        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    trySend(client)
                } else {
                    close(IllegalStateException("Billing setup failed: ${billingResult.debugMessage}"))
                }
            }

            override fun onBillingServiceDisconnected() {
                client.startConnection(this)
            }
        })

        awaitClose {
            client.endConnection()
        }
    }.onEach { client ->
        checkUnconsumedPurchases(client)
    }.catch {
        Timber.e(it, "Failed to init billing client")
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    private val productDetails = billingClient.flatMapLatest { client ->
        flow {
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(PREMIUM_ACCOUNT_PRODUCT_ID)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build(),
                    ),
                )
                .build()

            suspendCancellableCoroutine { continuation ->
                client?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        continuation.resume(productDetailsList.firstOrNull())
                    } else {
                        continuation.resumeWithException(
                            IllegalStateException("Failed to query product: ${billingResult.debugMessage}"),
                        )
                    }
                }
            }.also { emit(it) }
        }
    }.catch {
        Timber.e(it, "Failed to get product details")
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    val productUiState = productDetails
        .map { product ->
            when (product) {
                null -> ProductUiState.NotAvailable
                else -> ProductUiState.Ready(product.toUiModel())
            }
        }
        .catch { Timber.e(it, "Failed to get product details") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProductUiState.Loading,
        )

    private fun createBillingClient(listener: PurchasesUpdatedListener): BillingClient =
        BillingClient.newBuilder(context)
            .setListener(listener)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build(),
            )
            .build()

    private fun checkUnconsumedPurchases(client: BillingClient?) {
        client?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        consumePurchase(purchase.purchaseToken)
                    }
                }
            } else {
                Timber.e("Failed to query purchases: ${billingResult.debugMessage}")
            }
        }
    }

    private fun sendEvent(event: PremiumUiEvent) {
        viewModelScope.launch { _uiEvent.emit(event) }
    }

    fun purchase(activity: Activity) {
        viewModelScope.launch {
            val client = billingClient.firstOrNull() ?: return@launch
            val product = productDetails.firstOrNull() ?: return@launch

            try {
                val params = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(product)
                                .build(),
                        ),
                    )
                    .build()

                val responseCode = client.launchBillingFlow(activity, params).responseCode
                check(responseCode == BillingClient.BillingResponseCode.OK) {
                    "Failed to launch billing flow"
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to purchase")
                sendEvent(PremiumUiEvent.PurchaseFailure)
            }
        }
    }

    private fun consumePurchase(purchaseToken: String) {
        viewModelScope.launch {
            _isPurchasing.value = true

            userRepository.upgradeToPremium(purchaseToken).fold(
                onSuccess = { PremiumUiEvent.PurchaseSuccess },
                onFailure = { PremiumUiEvent.PurchaseFailure },
            ).also { sendEvent(it) }
            _isPurchasing.value = false
        }
    }

    companion object {
        private const val PREMIUM_ACCOUNT_PRODUCT_ID = "premium_account"
    }
}
