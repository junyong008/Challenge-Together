package com.yjy.feature.premium

import android.app.Activity
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherButton
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.StableImage
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.premium.model.PremiumUiEvent
import com.yjy.feature.premium.model.ProductUiState
import com.yjy.feature.premium.model.getProductOrNull
import com.yjy.feature.premium.model.isLoading
import com.yjy.feature.premium.model.isReady
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun PremiumRoute(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PremiumViewModel = hiltViewModel(),
) {
    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()
    val isPurchasing by viewModel.isPurchasing.collectAsStateWithLifecycle()
    val productUiState by viewModel.productUiState.collectAsStateWithLifecycle()

    PremiumScreen(
        modifier = modifier,
        isPremium = isPremium,
        isPurchasing = isPurchasing,
        productUiState = productUiState,
        uiEvent = viewModel.uiEvent,
        onBackClick = onBackClick,
        purchasePremium = viewModel::purchase,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun PremiumScreen(
    modifier: Modifier = Modifier,
    isPremium: Boolean = false,
    isPurchasing: Boolean = false,
    productUiState: ProductUiState = ProductUiState.Loading,
    uiEvent: Flow<PremiumUiEvent> = flowOf(),
    onBackClick: () -> Unit = {},
    purchasePremium: (activity: Activity) -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val purchaseCompleteMessage = stringResource(id = R.string.feature_premium_purchase_complete)
    val purchaseErrorMessage = stringResource(id = R.string.feature_premium_error_occurred)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            PremiumUiEvent.PurchaseSuccess -> onShowSnackbar(SnackbarType.SUCCESS, purchaseCompleteMessage)
            PremiumUiEvent.PurchaseFailure -> onShowSnackbar(SnackbarType.ERROR, purchaseErrorMessage)
        }
    }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                onNavigationClick = onBackClick,
            )
        },
        bottomBar = {
            ChallengeTogetherButton(
                onClick = { purchasePremium(context as Activity) },
                enabled = productUiState.isReady() && !isPurchasing && !isPremium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                containerColor = CustomColorProvider.colorScheme.brandDim,
            ) {
                if (productUiState.isLoading() || isPurchasing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = CustomColorProvider.colorScheme.brand,
                    )
                } else {
                    Text(
                        text = if (isPremium) {
                            stringResource(id = R.string.feature_premium_active)
                        } else {
                            productUiState.getProductOrNull()?.price
                                ?: stringResource(id = R.string.feature_premium_invalid_now)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            TitleWithDescription(
                titleRes = R.string.feature_premium_title,
                descriptionRes = R.string.feature_premium_description,
            )
            Spacer(modifier = Modifier.height(70.dp))
            StableImage(
                drawableResId = R.drawable.image_fire,
                descriptionResId = R.string.feature_premium_info_graphic,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(150.dp),
            )
            Spacer(modifier = Modifier.height(70.dp))
            Benefits()
        }
    }
}

@Composable
private fun Benefits(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(CustomColorProvider.colorScheme.surface)
            .padding(16.dp),
    ) {
        Benefit(titleRes = R.string.feature_premium_no_ads)
        Spacer(modifier = Modifier.height(16.dp))
        Benefit(titleRes = R.string.feature_premium_unlock_widgets)
        Spacer(modifier = Modifier.height(16.dp))
        Benefit(titleRes = R.string.feature_premium_app_lock)
    }
}

@Composable
private fun Benefit(@StringRes titleRes: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = ImageVector.vectorResource(id = ChallengeTogetherIcons.CheckOnly),
            contentDescription = stringResource(id = titleRes),
            tint = CustomColorProvider.colorScheme.brand,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = titleRes),
            style = MaterialTheme.typography.bodySmall,
            color = CustomColorProvider.colorScheme.onSurface,
        )
    }
}

@DevicePreviews
@Composable
fun PremiumScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            PremiumScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
