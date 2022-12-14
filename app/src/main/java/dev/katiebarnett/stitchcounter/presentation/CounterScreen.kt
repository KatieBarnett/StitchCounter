package dev.katiebarnett.stitchcounter.presentation

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import dev.katiebarnett.stitchcounter.MainViewModel
import dev.katiebarnett.stitchcounter.R.plurals
import dev.katiebarnett.stitchcounter.R.string
import dev.katiebarnett.stitchcounter.data.models.Counter
import dev.katiebarnett.stitchcounter.getCounterProgress
import dev.katiebarnett.stitchcounter.presentation.theme.Charcoal
import dev.katiebarnett.stitchcounter.presentation.theme.Dimen
import dev.katiebarnett.stitchcounter.presentation.theme.Pink
import dev.katiebarnett.stitchcounter.presentation.theme.StitchCounterTheme

@Composable
fun CounterScreen(
    projectId: Int,
    counterId: Int,
    viewModel: MainViewModel,
    onCounterEdit: (counterName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val projectState = viewModel.getProject(projectId).collectAsState(initial = null)
    projectState.value?.let { project ->
        val counter = project.counters.firstOrNull { it.id == counterId }
        counter?.let {
            CounterContent(
                counter = it,
                onCounterUpdate = { updatedCounter ->
                    viewModel.updateCounter(project, updatedCounter)
                },
                onCounterReset = {
                    viewModel.resetCounter(project, counter)
                },
                onCounterEdit = { onCounterEdit.invoke(counter.name) },
                modifier = modifier
            )
        } // TODO - else display error
    } // TODO - else display error
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CounterContent(counter: Counter,
                   onCounterUpdate: (counter: Counter) -> Unit,
                   onCounterReset: () -> Unit,
                   onCounterEdit: () -> Unit,
                   modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        counter.getCounterProgress()?.let { progress ->
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
                    text = pluralStringResource(plurals.counter_label_fraction, counter.maxCount, counter.name, counter.currentCount, counter.maxCount),
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
                Text(
                    text = counter.currentCount.toString(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.display3,
                    modifier = Modifier.weight(1f)
                )
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

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun CounterContentPreview() {
    StitchCounterTheme {
        CounterContent(Counter(id = 3, name = "pattern", currentCount = 400, maxCount = 500), {}, {}, {})
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun CounterContentPreviewLarge() {
    StitchCounterTheme {
        CounterContent(Counter(id = 3, name = "pattern", currentCount = 400, maxCount = 500), {}, {}, {})
    }
}

@Preview(device = Devices.WEAR_OS_RECT, showSystemUi = true)
@Composable
fun CounterContentPreviewRect() {
    StitchCounterTheme {
        CounterContent(Counter(id = 3, name = "pattern", currentCount = 400, maxCount = 500), {}, {}, {})
    }
}

@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Composable
fun CounterContentPreviewSquare() {
    StitchCounterTheme {
        CounterContent(Counter(id = 3, name = "pattern", currentCount = 400, maxCount = 500), {}, {}, {})
    }
}