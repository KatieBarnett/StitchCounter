package dev.veryniche.stitchcounter.mobile.purchase

import com.android.billingclient.api.QueryProductDetailsParams

val appProductList =
    listOf<QueryProductDetailsParams.Product>()


val appSubscriptionList =
    listOf<QueryProductDetailsParams.Product>(
        getSubscriptionQuery(Products.bundle),
    )

object Products {
    private const val PRODUCT_PREFIX = "dev.veryniche.stitchcounter"

    const val bundle = "$PRODUCT_PREFIX.bundle"
    const val bundleOfferMonthly = "bundle"
    const val bundleOfferAnnual = "bundle-annual"
}
