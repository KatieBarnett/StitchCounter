package dev.veryniche.stitchcounter.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import dev.veryniche.stitchcounter.MainViewModel
import dev.veryniche.stitchcounter.R.string
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.presentation.theme.Dimen
import dev.veryniche.stitchcounter.util.Analytics
import dev.veryniche.stitchcounter.util.TrackedScreen
import dev.veryniche.stitchcounter.util.trackScreenView
import kotlinx.coroutines.launch

@Composable
fun SelectProjectForTileScreen(
    viewModel: MainViewModel,
    listState: ScalingLazyListState,
    onProjectClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val projects: List<Project> by viewModel.projects.collectAsState(initial = emptyList())
    SelectProjectForTileList(projects, listState, onProjectClick, modifier)
}

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun SelectProjectForTileList(
    projectList: List<Project>,
    listState: ScalingLazyListState,
    onProjectClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    TrackedScreen {
        trackScreenView(name = Analytics.Screen.SelectProjectForTile)
    }
    val focusRequester = rememberActiveFocusRequester()
    val coroutineScope = rememberCoroutineScope()
    ScalingLazyColumn(
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
        items(projectList) { project ->
            ProjectChip(project, onProjectClick)
        }
    }
}
