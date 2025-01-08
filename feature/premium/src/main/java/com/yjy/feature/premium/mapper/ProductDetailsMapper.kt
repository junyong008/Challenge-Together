package com.yjy.feature.premium.mapper

import com.android.billingclient.api.ProductDetails
import com.yjy.feature.premium.model.Product

internal fun ProductDetails.toUiModel() = Product(
    productId = productId,
    name = name,
    description = description,
    price = oneTimePurchaseOfferDetails?.formattedPrice ?: "",
)
