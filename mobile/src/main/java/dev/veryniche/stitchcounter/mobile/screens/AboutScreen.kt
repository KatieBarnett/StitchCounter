package dev.veryniche.stitchcounter.mobile.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat.startActivity
import dev.veryniche.stitchcounter.core.AnalyticsConstants
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.mobile.BuildConfig
import dev.veryniche.stitchcounter.mobile.TrackedScreen
import dev.veryniche.stitchcounter.mobile.components.CollapsedTopAppBar
import dev.veryniche.stitchcounter.mobile.previews.PreviewScreen
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseAction
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseStatus
import dev.veryniche.stitchcounter.mobile.trackEvent
import dev.veryniche.stitchcounter.mobile.trackScreenView
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@Composable
fun AboutHeading(textRes: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = textRes),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun AboutText(textRes: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = textRes),
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    purchaseStatus: PurchaseStatus,
    onPurchaseClick: (PurchaseAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollableState = rememberScrollState()
    val context = LocalContext.current
    TrackedScreen {
        trackScreenView(name = AnalyticsConstants.Screen.About)
    }
    Scaffold(
        topBar = {
            CollapsedTopAppBar(
                titleText = stringResource(id = R.string.about_title),
                actions = {},
                onNavigation = onNavigateBack
            )
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
                    trackEvent(AnalyticsConstants.Action.AboutEmail)
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.setData(Uri.parse("mailto:")) // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, email)
                    intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                    if (intent.resolveActivity(context.packageManager) != null) {
                        startActivity(context, intent, null)
                    }
                }
            )

            if (!(purchaseStatus.isAdRemovalPurchased || purchaseStatus.isBundlePurchased)) {
                AboutHeading(R.string.about_remove_ads_version_title)
                Button(content = {
                    Text(text = stringResource(id = R.string.about_get_remove_ads_version))
                }, onClick = {
                    trackEvent(AnalyticsConstants.Action.AboutRemoveAdsVersion)
                    onPurchaseClick.invoke(PurchaseAction.AD_REMOVAL)
                })
            }

            if (!(purchaseStatus.isSyncPurchased || purchaseStatus.isBundlePurchased)) {
                AboutHeading(R.string.about_sync_version_title)
                Text(
                    text = stringResource(id = R.string.about_sync_version),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(content = {
                    Text(text = stringResource(id = R.string.about_get_sync_version))
                }, onClick = {
                    trackEvent(AnalyticsConstants.Action.AboutSyncVersion)
                    onPurchaseClick.invoke(PurchaseAction.SYNC)
                })
            }

            if (!(purchaseStatus.isBundlePurchased)) {
                AboutHeading(R.string.about_bundle_version_title)
                Text(
                    text = stringResource(id = R.string.about_bundle_version),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(content = {
                    Text(text = stringResource(id = R.string.about_get_bundle_version))
                }, onClick = {
                    trackEvent(AnalyticsConstants.Action.AboutBundleVersion)
                    onPurchaseClick.invoke(PurchaseAction.BUNDLE)
                })
            }

            AboutHeading(R.string.about_developer_title)
            AboutText(R.string.about_developer_text)
            val aboutDeveloperUrl = stringResource(id = R.string.about_developer_url)
            Button(content = {
                Text(text = stringResource(id = R.string.about_developer))
            }, onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(aboutDeveloperUrl))
                context.startActivity(intent)
            })

            val privacyPolicyUrl = stringResource(id = R.string.about_privacy_policy_url)
            AboutHeading(R.string.about_privacy_policy_title)
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
        AboutScreen(purchaseStatus = PurchaseStatus(false, false, false), onNavigateBack = {}, onPurchaseClick = {})
    }
}

@PreviewScreen
@Composable
fun AboutScreenRemoveAdsPurchasedPreview() {
    StitchCounterTheme {
        AboutScreen(purchaseStatus = PurchaseStatus(true, false, false), onNavigateBack = {}, onPurchaseClick = {})
    }
}

@PreviewScreen
@Composable
fun AboutScreenSyncPurchasedPreview() {
    StitchCounterTheme {
        AboutScreen(purchaseStatus = PurchaseStatus(false, true, false), onNavigateBack = {}, onPurchaseClick = {})
    }
}

@PreviewScreen
@Composable
fun AboutScreenBundlePurchasedPreview() {
    StitchCounterTheme {
        AboutScreen(purchaseStatus = PurchaseStatus(false, false, true), onNavigateBack = {}, onPurchaseClick = {})
    }
}
