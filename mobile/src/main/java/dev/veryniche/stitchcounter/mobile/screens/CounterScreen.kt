package dev.veryniche.stitchcounter.mobile.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.veryniche.stitchcounter.core.Analytics.Action
import dev.veryniche.stitchcounter.core.Analytics.Screen
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.core.TrackedScreen
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.core.trackEvent
import dev.veryniche.stitchcounter.core.trackScreenView
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.mobile.BuildConfig
import dev.veryniche.stitchcounter.mobile.ads.BannerAd
import dev.veryniche.stitchcounter.mobile.ads.BannerAdLocation
import dev.veryniche.stitchcounter.mobile.components.AboutActionIcon
import dev.veryniche.stitchcounter.mobile.components.CounterCentre
import dev.veryniche.stitchcounter.mobile.components.DeleteActionIcon
import dev.veryniche.stitchcounter.mobile.components.DeleteCounterConfirmation
import dev.veryniche.stitchcounter.mobile.components.EditActionIcon
import dev.veryniche.stitchcounter.mobile.components.NavigationIcon
import dev.veryniche.stitchcounter.mobile.components.SaveCounterConfirmation
import dev.veryniche.stitchcounter.mobile.components.topAppBarColors
import dev.veryniche.stitchcounter.mobile.previews.PreviewScreen
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CounterScreen(
    counter: Counter,
    onAboutClick: () -> Unit,
    onCounterUpdate: (counter: Counter) -> Unit,
    onCounterDelete: () -> Unit,
    onBack: () -> Unit,
    inEditMode: Boolean = false,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier
) {
    val counterNameDefault = stringResource(R.string.counter_name_default)
    var counterName by rememberSaveable {
        mutableStateOf(
            counter.name.ifBlank {
                counterNameDefault
            }
        )
    }
    var showCounterEditMode by rememberSaveable { mutableStateOf(inEditMode) }
    var showDeleteCounterConfirmation by rememberSaveable { mutableStateOf(false) }
    var showSaveCounterConfirmation by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    TrackedScreen {
        trackScreenView(name = Screen.Counter, isMobile = true)
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = if (BuildConfig.SHOW_IDS) {
                            "$counterName (${counter.id})"
                        } else {
                            counterName
                        },
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                colors = topAppBarColors,
                actions = {
                    if (!showCounterEditMode) {
                        EditActionIcon { showCounterEditMode = true }
                    }
                    DeleteActionIcon { showDeleteCounterConfirmation = true }
                    AboutActionIcon { onAboutClick.invoke() }
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    NavigationIcon {
                        if (showCounterEditMode) {
                            showSaveCounterConfirmation = true
                        } else {
                            onBack.invoke()
                        }
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            val fabModifier = Modifier
            if (showCounterEditMode) {
                ExtendedFloatingActionButton(
                    onClick = {
                        trackEvent(Action.EditCounterSave, isMobile = true)
                        onCounterUpdate.invoke(counter.copy(name = counterName))
                        showCounterEditMode = false
                    },
                    icon = { Icon(Icons.Filled.Check, stringResource(id = R.string.add_counter)) },
                    text = { Text(text = stringResource(id = R.string.save_counter)) },
                    modifier = fabModifier
                )
            }
        },
        bottomBar = {
            if (!LocalInspectionMode.current) {
                BannerAd(
                    location = BannerAdLocation.CounterScreen,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .imePadding()
                )
            }
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        Column(
            Modifier.padding(contentPadding).padding(Dimen.spacingQuad)
        ) {
            OutlinedButton(
                onClick = {
                    if (counter.currentCount > 0) {
                        onCounterUpdate.invoke(counter.copy(currentCount = counter.currentCount - 1))
                    }
                },
                shape = RoundedCornerShape(CornerSize(Dimen.mobileCounterButtonCornerRadius)),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = stringResource(id = R.string.counter_subtract),
                    modifier = Modifier.fillMaxSize(0.8f)
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().weight(1f)
            ){
                CounterCentre(
                    name = counter.name,
                    currentCount = counter.currentCount,
                    maxCount = counter.maxCount,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(CornerSize(Dimen.mobileCounterButtonCornerRadius)),
                onClick = { onCounterUpdate.invoke(counter.copy(currentCount = counter.currentCount + 1)) },
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.counter_add),
                    modifier = Modifier.fillMaxSize(0.8f)
                )
            }
        }
        if (showSaveCounterConfirmation) {
            SaveCounterConfirmation(
                counterName = counterName,
                onAccept = {
                    trackEvent(Action.EditCounterSave, isMobile = true)
                    onCounterUpdate.invoke(counter.copy(name = counterName))
                    showCounterEditMode = false
                },
                onDismiss = {
                    onBack.invoke()
                }
            )
        }
        if (showDeleteCounterConfirmation) {
            DeleteCounterConfirmation(
                counterName = counterName,
                onAccept = {
                    onCounterDelete.invoke()
                },
                onDismiss = {
                    onBack.invoke()
                }
            )
        }
    }
}

@PreviewScreen
@Composable
fun CounterScreenPreview() {
    StitchCounterTheme {
        CounterScreen(
            Counter(id = 3, name = "pattern", currentCount = 4),
            onAboutClick = { },
            onCounterUpdate = { },
            onCounterDelete = { },
            onBack = { },
            inEditMode = false,
            snackbarHostState = remember { SnackbarHostState() },
            modifier = Modifier,
        )
    }
}

@PreviewScreen
@Composable
fun CounterScreenEditPreview() {
    StitchCounterTheme {
        CounterScreen(
            Counter(id = 3, name = "pattern", currentCount = 4),
            onAboutClick = { },
            onCounterUpdate = { },
            onCounterDelete = { },
            onBack = { },
            inEditMode = true,
            snackbarHostState = remember { SnackbarHostState() },
            modifier = Modifier,
        )
    }
}

@PreviewScreen
@Composable
fun CounterScreenLongTextPreview() {
    StitchCounterTheme {
        CounterScreen(
            counter = Counter(
                id = 3,
                name = "pattern that is something super long",
                currentCount = 4000,
                maxCount = 1000
            ),
            onAboutClick = { },
            onCounterUpdate = { },
            onCounterDelete = { },
            onBack = { },
            inEditMode = false,
            snackbarHostState = remember { SnackbarHostState() },
            modifier = Modifier,
        )
    }
}

@PreviewScreen
@Composable
fun CounterScreenVeryLongTextPreview() {
    StitchCounterTheme {
        CounterScreen(
            counter = Counter(
                id = 3,
                name = "pattern that is something super long",
                currentCount = 400000,
                maxCount = 100000
            ),
            onAboutClick = { },
            onCounterUpdate = { },
            onCounterDelete = { },
            onBack = { },
            inEditMode = false,
            snackbarHostState = remember { SnackbarHostState() },
            modifier = Modifier,
        )
    }
}
