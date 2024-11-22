package dev.veryniche.stitchcounter.wear.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import dev.veryniche.stitchcounter.R
import dev.veryniche.stitchcounter.core.Analytics
import dev.veryniche.stitchcounter.core.TrackedScreen
import dev.veryniche.stitchcounter.core.trackScreenView
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.wear.MainViewModel
import dev.veryniche.stitchcounter.wear.presentation.theme.Dimen
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
        trackScreenView(name = Analytics.Screen.SelectProjectForTile, isMobile = false)
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
                    stringResource(R.string.select_project_for_tile_header),
                    modifier = Modifier.padding(top = Dimen.spacingQuad)
                )
            }
        }
        items(projectList) { project ->
            ProjectChip(project, onProjectClick)
        }
        item {
            Text(
                text = stringResource(R.string.select_project_for_tile_footer),
                color = MaterialTheme.colors.onSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(Dimen.spacing)
            )
        }
    }
}
