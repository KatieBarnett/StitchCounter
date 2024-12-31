package dev.veryniche.stitchcounter.mobile.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.veryniche.stitchcounter.core.Analytics.Screen
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.core.TrackedScreen
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.core.trackScreenView
import dev.veryniche.stitchcounter.data.models.ScreenOnState
import dev.veryniche.stitchcounter.mobile.ads.BannerAd
import dev.veryniche.stitchcounter.mobile.ads.BannerAdLocation
import dev.veryniche.stitchcounter.mobile.components.AboutActionIcon
import dev.veryniche.stitchcounter.mobile.components.CollapsedTopAppBar
import dev.veryniche.stitchcounter.mobile.components.NavigationIcon
import dev.veryniche.stitchcounter.mobile.previews.PreviewScreen
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseStatus
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme
import dev.veryniche.stitchcounter.storage.ThemeMode

@Composable
fun SettingsHeading(textRes: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = textRes),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun SettingsText(textRes: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = textRes),
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun SettingsScreen(
    purchaseStatus: PurchaseStatus,
    onNavigateBack: () -> Unit,
    onAboutClick: () -> Unit,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onKeepScreenOnStateSelected: (ScreenOnState) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier,
    themeMode: ThemeMode = ThemeMode.Auto,
    keepScreenOnState: ScreenOnState = ScreenOnState()
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
            if (!LocalInspectionMode.current) {
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
            if (!purchaseStatus.isBundlePurchased) {
                Text(
                    text = stringResource(id = R.string.settings_purchase_pro),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Text(
                text = stringResource(id = R.string.settings_theme_heading),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
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
                                enabled = purchaseStatus.isBundlePurchased,
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
                            enabled = purchaseStatus.isBundlePurchased,
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
            Text(
                text = stringResource(id = R.string.settings_screen_on_heading),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            var projectScreenOnState by remember { mutableStateOf(keepScreenOnState.projectScreenOn) }
            var counterScreenOnState by remember { mutableStateOf(keepScreenOnState.counterScreenOn) }
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = projectScreenOnState,
                        enabled = purchaseStatus.isBundlePurchased,
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
                    enabled = purchaseStatus.isBundlePurchased,
                    onCheckedChange = null
                )
                Text(
                    text = stringResource(R.string.settings_screen_on_project),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = counterScreenOnState,
                        enabled = purchaseStatus.isBundlePurchased,
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
                    enabled = purchaseStatus.isBundlePurchased,
                    checked = counterScreenOnState,
                    onCheckedChange = null
                )
                Text(
                    text = stringResource(R.string.settings_screen_on_counter),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@PreviewScreen
@Composable
fun SettingsScreenPreview() {
    StitchCounterTheme {
        SettingsScreen(
            purchaseStatus = PurchaseStatus(),
            onNavigateBack = {},
            onAboutClick = {},
            onThemeModeSelected = {},
            onKeepScreenOnStateSelected = {}
        )
    }
}
