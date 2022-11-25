package dev.katiebarnett.stitchcounter.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.items
import androidx.wear.compose.material.rememberScalingLazyListState
import dev.katiebarnett.stitchcounter.MainViewModel
import dev.katiebarnett.stitchcounter.R.string
import dev.katiebarnett.stitchcounter.data.models.Counter
import dev.katiebarnett.stitchcounter.data.models.Project
import dev.katiebarnett.stitchcounter.getNextCounterId
import dev.katiebarnett.stitchcounter.presentation.theme.Dimen

@Composable
fun ProjectScreen(
    id: Int,
    viewModel: MainViewModel,
    listState: ScalingLazyListState,
    onCounterClick: (counter: Counter) -> Unit,
    onProjectEdit: (projectId: Int, projectName: String) -> Unit,
    onCounterAdd: (counterId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val project = viewModel.getProject(id).collectAsState(initial = null)
    project.value?.let { project ->
        val nextCounterId = project.getNextCounterId()
        ProjectContent(
            project = project,
            listState = listState,
            onCounterUpdate = { counter -> viewModel.updateCounter(project, counter) },
            onCounterAdd = { onCounterAdd.invoke(nextCounterId)},
            onProjectReset = { viewModel.resetProject(project) },
            onProjectEdit = { onProjectEdit.invoke(id, project.name) },
            onCounterClick = onCounterClick,
            modifier = modifier
        )
    } // TODO - else display error
}

@Composable
fun ProjectContent(
    project: Project,
    listState: ScalingLazyListState,
    onCounterAdd: () -> Unit,
    onCounterUpdate: (counter: Counter) -> Unit,
    onCounterClick: (counter: Counter) -> Unit,
    onProjectReset: () -> Unit,
    onProjectEdit: () -> Unit,
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
        item {
            ListHeader {
                ListTitle(project.name)
            }
        }
        items(project.counters) { counter ->
            CounterListItemComponent(
                counter = counter,
                onCounterUpdate = onCounterUpdate, 
                onCounterClick = onCounterClick,
                Modifier)
        }
        item {
            Row(horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()) {
                CompactButton(
                    onClick = { onCounterAdd.invoke() },
                    colors = ButtonDefaults.secondaryButtonColors()
                ) {
                    Icon(
                        imageVector = Filled.Add,
                        contentDescription = stringResource(id = string.add_counter)
                    )
                }
                Spacer(modifier = Modifier.width(Dimen.spacing))
                CompactButton(
                    onClick = { onProjectEdit.invoke() },
                    colors = ButtonDefaults.secondaryButtonColors()
                ) {
                    Icon(
                        imageVector = Filled.Edit,
                        contentDescription = stringResource(id = string.edit_project)
                    )
                }
                Spacer(modifier = Modifier.width(Dimen.spacing))
                CompactButton(
                    onClick = { onProjectReset.invoke() },
                    colors = ButtonDefaults.secondaryButtonColors()
                ) {
                    Icon(
                        imageVector = Filled.Refresh,
                        contentDescription = stringResource(id = string.reset_project)
                    )
                }
                
            }
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
        {},{},{}, {}, {},
        Modifier.fillMaxSize()
    )
}