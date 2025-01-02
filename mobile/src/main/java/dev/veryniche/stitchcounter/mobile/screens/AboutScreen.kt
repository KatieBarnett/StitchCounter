package dev.veryniche.stitchcounter.mobile.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.veryniche.stitchcounter.core.Analytics.Action
import dev.veryniche.stitchcounter.core.Analytics.Screen
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.core.TrackedScreen
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.core.trackEvent
import dev.veryniche.stitchcounter.core.trackScreenView
import dev.veryniche.stitchcounter.mobile.BuildConfig
import dev.veryniche.stitchcounter.mobile.ads.BannerAd
import dev.veryniche.stitchcounter.mobile.ads.BannerAdLocation
import dev.veryniche.stitchcounter.mobile.components.CollapsedTopAppBar
import dev.veryniche.stitchcounter.mobile.components.Heading
import dev.veryniche.stitchcounter.mobile.components.InfoText
import dev.veryniche.stitchcounter.mobile.components.NavigationIcon
import dev.veryniche.stitchcounter.mobile.components.SettingsActionIcon
import dev.veryniche.stitchcounter.mobile.components.UnorderedListText
import dev.veryniche.stitchcounter.mobile.previews.PreviewScreen
import dev.veryniche.stitchcounter.mobile.purchase.Products
import dev.veryniche.stitchcounter.mobile.purchase.Products.bundleOfferAnnual
import dev.veryniche.stitchcounter.mobile.purchase.Products.bundleOfferMonthly
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseAction
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseStatus
import dev.veryniche.stitchcounter.mobile.purchase.Subscription
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    onSettingsClick: () -> Unit,
    purchaseStatus: PurchaseStatus,
    onPurchaseClick: (PurchaseAction) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier,
    availableSubscriptions: List<Subscription>,
) {
    val scrollableState = rememberScrollState()
    val context = LocalContext.current
    TrackedScreen {
        trackScreenView(name = Screen.About, isMobile = true)
    }
    Scaffold(
        topBar = {
            CollapsedTopAppBar(
                titleText = stringResource(id = R.string.about_title),
                actions = {
                    SettingsActionIcon { onSettingsClick.invoke() }
                },
                navigationIcon = { NavigationIcon({ onNavigateBack.invoke() }) }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            if (!LocalInspectionMode.current && !purchaseStatus.isBundleSubscribed) {
                BannerAd(
                    location = BannerAdLocation.AboutScreen,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .imePadding()
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimen.spacingQuad),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollableState)
                .padding(Dimen.spacingDouble)
        ) {
            Text(
                text = stringResource(id = R.string.about_welcome),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(id = R.string.about_content_mobile),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(id = R.string.about_content_mobile_get_wear),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(id = R.string.about_feedback),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            val email = stringResource(id = R.string.about_email)
            val emailSubject = stringResource(id = R.string.about_email_subject)
            Text(
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                text = email,
                modifier = Modifier.clickable {
                    trackEvent(Action.AboutEmail, isMobile = true)
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.setData(Uri.parse("mailto:")) // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, email)
                    intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent, null)
                    }
                }
            )

            Heading(R.string.purchases_title)
            if (purchaseStatus.isBundleSubscribed) {
                InfoText(R.string.purchases_pro_description_purchased)
                UnorderedListText(
                    listOf(
                        R.string.purchases_pro_feature_1,
                        R.string.purchases_pro_feature_2,
                        R.string.purchases_pro_feature_3,
                        R.string.purchases_pro_feature_4,
                        R.string.purchases_pro_feature_5,
                    )
                )
                val manageSubscriptionUrl = stringResource(R.string.purchases_manage_subscription_url)
                Button(content = {
                    Text(text = stringResource(id = R.string.purchases_manage_subscription))
                }, onClick = {
                    trackEvent(Action.ManageSubscription, isMobile = true)
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(manageSubscriptionUrl))
                    context.startActivity(intent)
                })
            } else {
                InfoText(R.string.purchases_pro_description)
                UnorderedListText(
                    listOf(
                        R.string.purchases_pro_feature_1,
                        R.string.purchases_pro_feature_2,
                        R.string.purchases_pro_feature_3,
                        R.string.purchases_pro_feature_4,
                        R.string.purchases_pro_feature_5,
                    )
                )
                availableSubscriptions.forEach { subscription ->
                    subscription.plans?.forEach {
                        val buttonTextRes = when (it.planId) {
                        bundleOfferAnnual -> R.string.purchases_buy_pro_annual
                        bundleOfferMonthly -> R.string.purchases_buy_pro_monthly
                        else -> R.string.purchases_buy_pro_misc
                    }
                        Button(content = {
                            Text(text = stringResource(buttonTextRes, it.displayedPrice))
                        }, onClick = {
                            trackEvent("${Action.PurchasePro} ${it.planId}", isMobile = true)
                            onPurchaseClick.invoke(
                                PurchaseAction.Subscribe(subscription.productId, it.offerToken)
                            )
                        })
                    }
                }
            }

            Heading(R.string.about_developer_title)
            InfoText(R.string.about_developer_text)
            val aboutDeveloperUrl = stringResource(id = R.string.about_developer_url)
            Button(content = {
                Text(text = stringResource(id = R.string.about_developer))
            }, onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(aboutDeveloperUrl))
                context.startActivity(intent)
            })

            val privacyPolicyUrl = stringResource(id = R.string.about_privacy_policy_url)
            Heading(R.string.about_privacy_policy_title)
            Button(content = {
                Text(text = stringResource(id = R.string.about_privacy_policy))
            }, onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                context.startActivity(intent)
            })

//            if (BuildConfig.DEBUG) {
//                Spacer(modifier = Modifier.height(Dimen.spacingDouble))
//                Button(content = {
//                    Text(stringResource(id = R.string.navigate_showkase))
//                }, onClick = {
//                    ContextCompat.startActivity(context, Showkase.getBrowserIntent(context), null)
//                })
//            }

            Spacer(modifier = Modifier.height(Dimen.spacingDouble))
            Text(
                text = stringResource(id = R.string.about_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = modifier.fillMaxWidth()
            )
        }
    }
}

@PreviewScreen
@Composable
fun AboutScreenFreePreview() {
    StitchCounterTheme {
        AboutScreen(
            onNavigateBack = {},
            onSettingsClick = {},
            purchaseStatus = PurchaseStatus(false),
            onPurchaseClick = {},
            availableSubscriptions = listOf(
                Subscription(
                    productId = Products.bundle,
                    productName = "Unlimited Bundle",
                    productDescription = "Unlimited Bundle Description",
                    purchased = false,
                    plans = listOf(
                        Subscription.Plan(
                            planId = bundleOfferMonthly,
                            offerToken = "",
                            purchasePrice = "1.00",
                            purchaseCurrency = "AUD",
                        ),
                        Subscription.Plan(
                            planId = bundleOfferAnnual,
                            offerToken = "",
                            purchasePrice = "11.00",
                            purchaseCurrency = "AUD",
                        )
                    )
                )
            ),
        )
    }
}

@PreviewScreen
@Composable
fun AboutScreenBundlePurchasedPreview() {
    StitchCounterTheme {
        AboutScreen(
            onNavigateBack = {},
            onSettingsClick = {},
            purchaseStatus = PurchaseStatus(true),
            onPurchaseClick = {},
            availableSubscriptions = listOf(),
        )
    }
}
