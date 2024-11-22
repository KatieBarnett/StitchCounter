package dev.veryniche.stitchcounter.wear.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Confirmation
import dev.veryniche.stitchcounter.R
import dev.veryniche.stitchcounter.core.Analytics
import dev.veryniche.stitchcounter.core.TrackedScreen
import dev.veryniche.stitchcounter.core.trackScreenView
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.wear.MainViewModel
import dev.veryniche.stitchcounter.wear.presentation.theme.Dimen
import dev.veryniche.stitchcounter.wear.presentation.theme.StitchCounterTheme
import dev.veryniche.stitchcounter.wear.presentation.theme.stitchCounterColorPalette
import dev.veryniche.stitchcounter.wear.previews.PreviewComponent
import kotlinx.coroutines.launch

@Composable
fun SelectCounterForTileScreen(
    projectId: Int,
    viewModel: MainViewModel,
    listState: ScalingLazyListState,
    onCounterClick: (Int) -> Unit,
    onConfirmationDialogClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTileCounterConfirmation by remember { mutableStateOf(false) }
    var selectedCounterName by remember { mutableStateOf("") }
    val project = viewModel.getProject(projectId).collectAsState(initial = null)
    project.value?.let {
        SelectCounterForTileList(it.counters, listState, { id, name ->
            selectedCounterName = name
            onCounterClick.invoke(id)
            showTileCounterConfirmation = true
        }, modifier)
    }
    if (showTileCounterConfirmation) {
        CounterTileStateConfirmation(
            counterName = selectedCounterName,
            onTimeout = {
                showTileCounterConfirmation = false
                onConfirmationDialogClose.invoke()
            }
        )
    }
}

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun SelectCounterForTileList(
    counterList: List<Counter>,
    listState: ScalingLazyListState,
    onCounterClick: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    TrackedScreen {
        trackScreenView(name = Analytics.Screen.SelectCounterForTile, isMobile = false)
    }
    val focusRequester = rememberActiveFocusRequester()
    val coroutineScope = rememberCoroutineScope()

    ScalingLazyColumn(
        verticalArrangement = Arrangement.spacedBy(Dimen.spacingDouble),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .selectableGroup()
            .onRotaryScrollEvent {
                coroutineScope.launch {
                    listState.scrollBy(it.verticalScrollPixels)
                    listState.animateScrollBy(0f)
                }
                true
            }
            .focusRequester(focusRequester)
            .focusable(),
        state = listState,
    ) {
        item {
            ListHeader(Modifier) {
                ListTitle(
                    stringResource(R.string.select_counter_for_tile_header),
                    modifier = Modifier.padding(top = Dimen.spacingQuad)
                )
            }
        }
        items(counterList) { counter ->
            CounterChip(counter, onCounterClick)
        }
        item {
            Text(
                text = stringResource(R.string.select_counter_for_tile_footer),
                color = MaterialTheme.colors.onSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(Dimen.spacing)
            )
        }
    }
}

@Composable
fun CounterChip(counter: Counter, onCounterClick: (Int, String) -> Unit, modifier: Modifier = Modifier) {
    Chip(
        colors = ChipDefaults.primaryChipColors(),
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        label = {
            Text(
                text = counter.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        secondaryLabel = {
            if (LocalDensity.current.fontScale < 1.3f) {
                Text(
                    text = if (counter.maxCount == 0) {
                        stringResource(R.string.counter_max_label_zero)
                    } else {
                        stringResource(R.string.counter_max_label_many, counter.maxCount)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        onClick = {
            onCounterClick.invoke(counter.id, counter.name)
        }
    )
}

@Composable
fun CounterTileStateConfirmation(counterName: String, onTimeout: () -> Unit) {
    val content = buildAnnotatedString {
        append(stringResource(R.string.counter_tile_counter_selected))
        append(" ")
        pushStyle(SpanStyle(color = stitchCounterColorPalette.onSecondary))
        append(counterName)
        toAnnotatedString()
    }
    Confirmation(
        onTimeout = onTimeout,
        icon = {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = content.text,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(Dimen.confirmationIconSize)
            )
        },
        content = {
            Text(
                text = content,
                textAlign = TextAlign.Center
            )
        }
    )
}

@PreviewComponent
@Composable
fun CounterTileStateConfirmationPreview() {
    StitchCounterTheme {
        CounterTileStateConfirmation(
            counterName = "Counter name really long",
            onTimeout = {}
        )
    }
}

@PreviewComponent
@Composable
fun CounterChipPreview() {
    StitchCounterTheme {
        CounterChip(
            counter = Counter(
                id = 3,
                name = "pattern",
                currentCount = 40000,
                maxCount = 50000,
            ),
            { _, _ -> },
            Modifier
        )
    }
}
