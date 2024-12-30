package dev.veryniche.stitchcounter.mobile.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import dev.veryniche.stitchcounter.mobile.components.DeleteActionIcon
import dev.veryniche.stitchcounter.mobile.components.DeleteCounterConfirmation
import dev.veryniche.stitchcounter.mobile.components.EditActionIcon
import dev.veryniche.stitchcounter.mobile.components.NavigationIcon
import dev.veryniche.stitchcounter.mobile.components.SaveCounterConfirmation
import dev.veryniche.stitchcounter.mobile.components.topAppBarColors
import dev.veryniche.stitchcounter.mobile.previews.PreviewScreen
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun CounterScreen(
    counter: Counter,
    onAboutClick: () -> Unit,
    onCounterUpdate: (counter: Counter) -> Unit,
    onCounterDelete: () -> Unit,
    onBack: () -> Unit,
    initialInEditMode: Boolean = false,
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
    var counterMaxCount by rememberSaveable { mutableStateOf<String>(counter.maxCount.toString()) }
    var showCounterEditMode by rememberSaveable { mutableStateOf(initialInEditMode) }
    var showDeleteCounterConfirmation by rememberSaveable { mutableStateOf(false) }
    var showSaveCounterConfirmation by rememberSaveable { mutableStateOf(false) }
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
                        overflow = TextOverflow.Ellipsis,
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
                        onCounterUpdate.invoke(
                            counter.copy(
                                name = counterName.trim(),
                                maxCount = counterMaxCount.trim().toIntOrNull() ?: 0
                            )
                        )
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
                enabled = !showCounterEditMode,
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
            ) {
                if (showCounterEditMode) {
                    Surface {
                        Column {
                            Row(
                                modifier = Modifier.padding(
                                    start = Dimen.spacingTriple,
                                    end = Dimen.spacingTriple,
                                    top = Dimen.spacingTriple
                                )
                            ) {
                                OutlinedTextField(
                                    value = counterName,
                                    onValueChange = { counterName = it },
                                    isError = counterName.isBlank(),
                                    label = {
                                        Text(
                                            stringResource(
                                                dev.veryniche.stitchcounter.mobile.R.string.label_counter_name
                                            )
                                        )
                                    },
                                    supportingText = {
                                        if (counterName.isBlank()) {
                                            Text(
                                                text = stringResource(
                                                    dev.veryniche.stitchcounter.mobile.R.string.validation_message_counter_name
                                                ),
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Row(
                                modifier = Modifier.padding(
                                    start = Dimen.spacingTriple,
                                    end = Dimen.spacingTriple,
                                    bottom = Dimen.spacingTriple
                                )
                            ) {
                                OutlinedTextField(
                                    value = counterMaxCount.toString(),
                                    isError = counterMaxCount.trim().toIntOrNull() == null,
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    onValueChange = {
                                        counterMaxCount = it
                                    },
                                    supportingText = {
                                        if (counterMaxCount.trim().toIntOrNull() == null) {
                                            Text(
                                                text = stringResource(
                                                    dev.veryniche.stitchcounter.mobile.R.string.validation_message_counter_max_count
                                                ),
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            stringResource(
                                                dev.veryniche.stitchcounter.mobile.R.string.label_counter_max_count
                                            )
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(Dimen.spacingTriple),
                                modifier = Modifier.padding(
                                    start = Dimen.spacingTriple,
                                    end = Dimen.spacingTriple,
                                    bottom = Dimen.spacingTriple
                                )
                            ) {
                                Button(
                                    onClick = {
                                        val updatedCounter = counter.copy(
                                            name = counterName,
                                            maxCount = counterMaxCount.trim().toIntOrNull() ?: 0
                                        )
                                        onCounterUpdate.invoke(updatedCounter)
                                        counterMaxCount = updatedCounter.maxCount.toString()
                                    },
                                    colors = ButtonDefaults.buttonColors(),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = stringResource(id = R.string.save_counter)
                                    )
                                    Text(stringResource(dev.veryniche.stitchcounter.mobile.R.string.save_counter_short))
                                }
                                Button(
                                    onClick = {
                                        trackEvent(Action.ResetCounter, isMobile = true)
                                        onCounterUpdate.invoke(counter.copy(currentCount = 0))
                                    },
                                    colors = ButtonDefaults.filledTonalButtonColors()
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = stringResource(id = R.string.reset_counter)
                                    )
                                    Text(
                                        stringResource(dev.veryniche.stitchcounter.mobile.R.string.reset_counter_short)
                                    )
                                }
                                Button(
                                    onClick = {
                                        trackEvent(Action.DeleteCounter, isMobile = true)
                                        onCounterDelete.invoke()
                                    },
                                    colors = ButtonDefaults.filledTonalButtonColors()
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = stringResource(id = R.string.delete_counter)
                                    )
                                    Text(
                                        stringResource(dev.veryniche.stitchcounter.mobile.R.string.delete_counter_short)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    CounterCentreLarge(
                        currentCount = counter.currentCount,
                        maxCount = counter.maxCount,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth().weight(1f),
                enabled = !showCounterEditMode,
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

@Composable
fun CounterCentreLarge(
    currentCount: Int,
    maxCount: Int,
    modifier: Modifier = Modifier
) {
    var showMaxCount by remember(maxCount) { mutableStateOf(maxCount > 0) }
    val currentCountFontSizeSmall = MaterialTheme.typography.displayLarge.fontSize
    val currentCountFontSizeLarge = MaterialTheme.typography.displayLarge.fontSize * 1.5
    var currentCountFontSize by remember { mutableStateOf(currentCountFontSizeLarge) }
    val maxCountFontSizeSmall = MaterialTheme.typography.displayMedium.fontSize
    val maxCountFontSizeLarge = MaterialTheme.typography.displayLarge.fontSize
    var maxCountFontSize by remember { mutableStateOf(maxCountFontSizeLarge) }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(
            text = currentCount.toString(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayLarge,
            fontSize = currentCountFontSize,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            onTextLayout = { result ->
                if (result.hasVisualOverflow && currentCountFontSize == currentCountFontSizeLarge) {
                    currentCountFontSize = currentCountFontSizeSmall
                }
            },
        )
        if (showMaxCount) {
            Text(
                text = "/$maxCount",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge,
                fontSize = maxCountFontSize,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                onTextLayout = { result ->
                    if (result.hasVisualOverflow && maxCountFontSize == maxCountFontSizeLarge) {
                        maxCountFontSize = maxCountFontSizeSmall
                    }
                },
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
            initialInEditMode = false,
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
            initialInEditMode = true,
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
            initialInEditMode = false,
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
                name = "pattern that is something super long that will overflow all bounds",
                currentCount = 400999000,
                maxCount = 100099900
            ),
            onAboutClick = { },
            onCounterUpdate = { },
            onCounterDelete = { },
            onBack = { },
            initialInEditMode = false,
            snackbarHostState = remember { SnackbarHostState() },
            modifier = Modifier,
        )
    }
}
