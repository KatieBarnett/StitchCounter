package dev.veryniche.stitchcounter.wear.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.ProgressIndicatorSegment
import com.google.android.horologist.composables.SquareSegmentedProgressIndicator
import dev.veryniche.stitchcounter.wear.MainViewModel
import dev.veryniche.mobile.R
import dev.veryniche.mobile.R.string
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.wear.getCounterProgress
import dev.veryniche.mobile.core.theme.Charcoal
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.mobile.core.theme.Pink
import dev.veryniche.stitchcounter.wear.presentation.theme.StitchCounterTheme
import dev.veryniche.stitchcounter.previews.PreviewScreen
import dev.veryniche.stitchcounter.wear.util.Analytics
import dev.veryniche.stitchcounter.wear.util.TrackedScreen
import dev.veryniche.stitchcounter.wear.util.trackScreenView
import kotlinx.coroutines.launch

@Composable
fun CounterScreen(
    projectId: Int,
    counterId: Int,
    viewModel: MainViewModel,
    onCounterEdit: (counterName: String, counterMax: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val composableScope = rememberCoroutineScope()
    val projectState = viewModel.getProject(projectId).collectAsState(initial = null)
    projectState.value?.let { project ->
        val counter = project.counters.firstOrNull { it.id == counterId }
        counter?.let {
            TrackedScreen {
                trackScreenView(name = Analytics.Screen.Counter)
            }
            CounterContent(
                counter = it,
                onCounterUpdate = { updatedCounter ->
                    composableScope.launch {
                        viewModel.updateCounter(project, updatedCounter)
                    }
                },
                onCounterReset = {
                    composableScope.launch {
                        viewModel.resetCounter(project, counter)
                    }
                },
                onCounterEdit = { onCounterEdit.invoke(counter.name, counter.maxCount) },
                modifier = modifier
            )
        } // TODO - else display error
    } // TODO - else display error
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalHorologistApi::class)
@Composable
fun CounterContent(counter: Counter,
                   onCounterUpdate: (counter: Counter) -> Unit,
                   onCounterReset: () -> Unit,
                   onCounterEdit: () -> Unit,
                   modifier: Modifier = Modifier) {

    var useCompactButton by remember { mutableStateOf(false) }
    val isRound = LocalConfiguration.current.isScreenRound

    Box(modifier = modifier.fillMaxSize()) {
        counter.getCounterProgress()?.let { progress ->
            if (isRound) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = Dimen.progressIndicatorPadding),
                    progress = progress,
                    indicatorColor = Pink,
                    trackColor = Charcoal,
                    strokeWidth = Dimen.progressIndicatorWidth,
                    startAngle = 315f,
                    endAngle = 225f
                )
            } else {
                SquareSegmentedProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = Dimen.progressIndicatorSquareTop,
                            start = Dimen.progressIndicatorPadding,
                            end = Dimen.progressIndicatorPadding,
                            bottom = Dimen.progressIndicatorPadding
                        ),
                    progress = progress,
                    trackSegments = listOf(
                        ProgressIndicatorSegment(
                            indicatorBrush = Brush.horizontalGradient(colors = listOf(Pink, Pink)),
                            weight = 1f
                        )
                    ),
                    trackColor = Charcoal,
                    strokeWidth = Dimen.progressIndicatorWidth,
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimen.withinProgressIndicatorPadding)) {
            Row(horizontalArrangement = Arrangement.spacedBy(Dimen.spacing),
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .weight(1f)) {
                Text(
                    text = if (counter.maxCount == 0) {
                        stringResource(R.string.counter_label_fraction_zero, counter.name)
                    } else {
                        stringResource(R.string.counter_label_fraction_many, counter.name,counter.currentCount, counter.maxCount)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimen.spacing)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Dimen.spacing),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                if (useCompactButton) {
                    CompactButton(
                        onClick = {
                            if (counter.currentCount > 0) {
                                onCounterUpdate.invoke(counter.copy(currentCount = counter.currentCount - 1))
                            }
                        },
                        colors = ButtonDefaults.secondaryButtonColors()
                    ) {
                        Icon(
                            imageVector = Filled.Remove,
                            contentDescription = stringResource(id = string.counter_subtract)
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            if (counter.currentCount > 0) {
                                onCounterUpdate.invoke(counter.copy(currentCount = counter.currentCount - 1))
                            }
                        },
                        colors = ButtonDefaults.secondaryButtonColors()
                    ) {
                        Icon(
                            imageVector = Filled.Remove,
                            contentDescription = stringResource(id = string.counter_subtract)
                        )
                    }
                }
                Text(
                    text = counter.currentCount.toString(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.display3,
                    modifier = Modifier.weight(1f),
                    onTextLayout = { textLayoutResult ->
                        if (!useCompactButton) {
                            useCompactButton = textLayoutResult.lineCount > 1
                        }
                    }
                )
                if (useCompactButton) {
                    CompactButton(
                        onClick = { onCounterUpdate.invoke(counter.copy(currentCount = counter.currentCount + 1)) },
                        colors = ButtonDefaults.primaryButtonColors()
                    ) {
                        Icon(
                            imageVector = Filled.Add,
                            contentDescription = stringResource(id = string.counter_add)
                        )
                    }
                } else {
                    Button(
                        onClick = { onCounterUpdate.invoke(counter.copy(currentCount = counter.currentCount + 1)) },
                        colors = ButtonDefaults.primaryButtonColors()
                    ) {
                        Icon(
                            imageVector = Filled.Add,
                            contentDescription = stringResource(id = string.counter_add)
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .weight(1f)) {
                CompactButton(
                    onClick = { onCounterEdit.invoke() },
                    colors = ButtonDefaults.secondaryButtonColors()
                ) {
                    Icon(
                        imageVector = Filled.Edit,
                        contentDescription = stringResource(id = string.edit_counter)
                    )
                }
                Spacer(modifier = Modifier.width(Dimen.spacing))
                CompactButton(
                    onClick = { onCounterReset.invoke() },
                    colors = ButtonDefaults.secondaryButtonColors()
                ) {
                    Icon(
                        imageVector = Filled.Refresh,
                        contentDescription = stringResource(id = string.reset_counter)
                    )
                }
            }
        }
    }
}

@PreviewScreen
@Composable
fun CounterContentPreview() {
    StitchCounterTheme {
        CounterContent(Counter(id = 3, name = "pattern", currentCount = 40000, maxCount = 50000), {}, {}, {})
    }
}