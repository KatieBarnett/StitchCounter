package dev.veryniche.stitchcounter.wear.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import dev.veryniche.stitchcounter.wear.MainViewModel
import dev.veryniche.mobile.R.string
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.wear.getNextCounterId
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.previews.PreviewScreen
import dev.veryniche.stitchcounter.wear.util.Analytics
import dev.veryniche.stitchcounter.wear.util.TrackedScreen
import dev.veryniche.stitchcounter.wear.util.trackEvent
import dev.veryniche.stitchcounter.wear.util.trackProjectScreenView
import kotlinx.coroutines.launch

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
    val composableScope = rememberCoroutineScope()
    val project = viewModel.getProject(id).collectAsState(initial = null)
    project.value?.let { project ->
        TrackedScreen {
            trackProjectScreenView(project.counters.size)
        }
        val nextCounterId = project.getNextCounterId()
        ProjectContent(
            project = project,
            listState = listState,
            onCounterUpdate = { counter ->
                composableScope.launch {
                    viewModel.updateCounter(project, counter)
                }
            },
            onCounterAdd = { onCounterAdd.invoke(nextCounterId)},
            onProjectReset = {
                composableScope.launch {
                    viewModel.resetProject(project)
                }
            },
            onProjectEdit = { onProjectEdit.invoke(id, project.name) },
            onCounterClick = onCounterClick,
            modifier = modifier
        )
    } // TODO - else display error
}

@OptIn(ExperimentalWearFoundationApi::class)
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
    val focusRequester = rememberActiveFocusRequester()
    val coroutineScope = rememberCoroutineScope()
    ScalingLazyColumn (
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
        state = listState
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
                    onClick = {
                        trackEvent(Analytics.Action.ResetProject)
                        onProjectReset.invoke()
                              },
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

@PreviewScreen
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
                    name = "pattern that is very long",
                    currentCount = 700
                ),
                Counter(
                    id = 3,
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