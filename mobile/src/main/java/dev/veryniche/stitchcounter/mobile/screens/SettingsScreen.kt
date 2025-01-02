package dev.veryniche.stitchcounter.mobile.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.veryniche.stitchcounter.core.Analytics.Action
import dev.veryniche.stitchcounter.core.Analytics.Screen
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.core.TrackedScreen
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.core.trackEvent
import dev.veryniche.stitchcounter.core.trackScreenView
import dev.veryniche.stitchcounter.data.models.ScreenOnState
import dev.veryniche.stitchcounter.mobile.ads.BannerAd
import dev.veryniche.stitchcounter.mobile.ads.BannerAdLocation
import dev.veryniche.stitchcounter.mobile.components.AboutActionIcon
import dev.veryniche.stitchcounter.mobile.components.CollapsedTopAppBar
import dev.veryniche.stitchcounter.mobile.components.Heading
import dev.veryniche.stitchcounter.mobile.components.InfoText
import dev.veryniche.stitchcounter.mobile.components.NavigationIcon
import dev.veryniche.stitchcounter.mobile.components.OptionText
import dev.veryniche.stitchcounter.mobile.components.UnorderedListText
import dev.veryniche.stitchcounter.mobile.previews.PreviewScreen
import dev.veryniche.stitchcounter.mobile.purchase.Products
import dev.veryniche.stitchcounter.mobile.purchase.Products.bundleOfferAnnual
import dev.veryniche.stitchcounter.mobile.purchase.Products.bundleOfferMonthly
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseAction
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseStatus
import dev.veryniche.stitchcounter.mobile.purchase.Subscription
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme
import dev.veryniche.stitchcounter.storage.ThemeMode

@Composable
fun SettingsScreen(
    purchaseStatus: PurchaseStatus,
    onNavigateBack: () -> Unit,
    onAboutClick: () -> Unit,
    onPurchaseClick: (PurchaseAction) -> Unit,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onKeepScreenOnStateSelected: (ScreenOnState) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier,
    themeMode: ThemeMode = ThemeMode.Auto,
    keepScreenOnState: ScreenOnState = ScreenOnState(),
    availableSubscriptions: List<Subscription>,
) {
    val scrollableState = rememberScrollState()
    val context = LocalContext.current
    TrackedScreen {
        trackScreenView(name = Screen.Settings, isMobile = true)
    }
    Scaffold(
        topBar = {
            CollapsedTopAppBar(
                titleText = stringResource(id = R.string.settings_title),
                actions = {
                    AboutActionIcon { onAboutClick.invoke() }
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
                    location = BannerAdLocation.SettingsScreen,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .imePadding()
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimen.spacingDouble),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollableState)
                .padding(Dimen.spacingDouble)
        ) {
            if (!purchaseStatus.isBundleSubscribed) {
                InfoText(R.string.settings_purchase_pro, bold = true)
            }
            Heading(R.string.settings_theme_heading)
            val radioOptions = ThemeMode.entries
            val (selectedOption, onOptionSelected) = remember {
                mutableStateOf(radioOptions.firstOrNull { it == themeMode } ?: ThemeMode.Auto)
            }
            // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
            Column(
                modifier = modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                radioOptions.forEach { entry ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (entry == selectedOption),
                                enabled = purchaseStatus.isBundleSubscribed,
                                onClick = {
                                    onOptionSelected(entry)
                                    onThemeModeSelected.invoke(entry)
                                },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (entry == selectedOption),
                            enabled = purchaseStatus.isBundleSubscribed,
                            onClick = null // null recommended for accessibility with screen readers
                        )
                        Column(Modifier.padding(start = 16.dp)) {
                            Text(
                                text = entry.name,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            entry.subtitle?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier)
            Heading(R.string.settings_screen_on_heading)
            var projectScreenOnState by remember { mutableStateOf(keepScreenOnState.projectScreenOn) }
            var counterScreenOnState by remember { mutableStateOf(keepScreenOnState.counterScreenOn) }
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = projectScreenOnState,
                        enabled = purchaseStatus.isBundleSubscribed,
                        onClick = {
                            val newValue = !projectScreenOnState
                            projectScreenOnState = newValue
                            onKeepScreenOnStateSelected.invoke(keepScreenOnState.copy(projectScreenOn = newValue))
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = projectScreenOnState,
                    enabled = purchaseStatus.isBundleSubscribed,
                    onCheckedChange = null
                )
                OptionText(R.string.settings_screen_on_project)
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = counterScreenOnState,
                        enabled = purchaseStatus.isBundleSubscribed,
                        onClick = {
                            val newValue = !counterScreenOnState
                            counterScreenOnState = newValue
                            onKeepScreenOnStateSelected.invoke(keepScreenOnState.copy(counterScreenOn = newValue))
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    enabled = purchaseStatus.isBundleSubscribed,
                    checked = counterScreenOnState,
                    onCheckedChange = null
                )
                OptionText(R.string.settings_screen_on_counter)
            }
            Spacer(Modifier)
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
        }
    }
}

@PreviewScreen
@Composable
fun SettingsScreenFreePreview() {
    StitchCounterTheme {
        SettingsScreen(
            purchaseStatus = PurchaseStatus(false),
            onNavigateBack = {},
            onAboutClick = {},
            onThemeModeSelected = {},
            onKeepScreenOnStateSelected = {},
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
                            purchasePrice = "1.00",
                            offerToken = "",
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
fun SettingsScreenBundlePurchasedPreview() {
    StitchCounterTheme {
        SettingsScreen(
            purchaseStatus = PurchaseStatus(true),
            onNavigateBack = {},
            onAboutClick = {},
            onThemeModeSelected = {},
            onKeepScreenOnStateSelected = {},
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
