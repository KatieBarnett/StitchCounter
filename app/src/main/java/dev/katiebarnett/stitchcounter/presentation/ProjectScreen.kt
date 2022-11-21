package dev.katiebarnett.stitchcounter.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.items
import androidx.wear.compose.material.rememberScalingLazyListState
import dev.katiebarnett.stitchcounter.MainViewModel
import dev.katiebarnett.stitchcounter.data.models.Counter
import dev.katiebarnett.stitchcounter.data.models.Project

@Composable
fun ProjectScreen(id: Int, viewModel: MainViewModel, listState: ScalingLazyListState, modifier: Modifier = Modifier) {
    val project = viewModel.getProject(id).collectAsState(initial = listOf())
    if (project.value.isNotEmpty()) {
        ProjectContent(
            project = project.value.first(),
            listState = listState,
            onCounterUpdate = { viewModel.updateCounter(it)},
            modifier = modifier
        )
    } else {
        // TODO - display error
    }
}

@Composable
fun ProjectContent(
    project: Project,
    listState: ScalingLazyListState,
    onCounterUpdate: (counter: Counter) -> Unit,
    modifier: Modifier = Modifier
) {
    ScalingLazyColumn (
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .selectableGroup(),
        state = listState,
        autoCentering = AutoCenteringParams(itemIndex = 0),
    ) {
        items(project.counters) { counter ->
            Counter(
                counter = counter,
                onCounterUpdate = {
                    onCounterUpdate.invoke(it)
            }, Modifier)
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun ProjectContentPreview() {
    ProjectContent(
        project = Project(
            id = 0,
            name = "shawl",
            counters = listOf(
                Counter(
                    id = 1,
                    name = "pattern",
                    currentCount = 7
                ),
                Counter(
                    id = 2,
                    name = "plain",
                    currentCount = 8
                )
            )
        ),
        listState = rememberScalingLazyListState(),
        onCounterUpdate = {},
        Modifier.fillMaxSize()
    )
}