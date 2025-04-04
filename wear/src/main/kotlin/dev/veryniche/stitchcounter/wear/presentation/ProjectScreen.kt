package dev.veryniche.stitchcounter.wear.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.ambient.AmbientState
import com.google.android.horologist.compose.ambient.AmbientStateUpdate
import com.google.android.horologist.compose.material.ResponsiveDialogContent
import dev.veryniche.stitchcounter.BuildConfig
import dev.veryniche.stitchcounter.R
import dev.veryniche.stitchcounter.R.string
import dev.veryniche.stitchcounter.core.Analytics
import dev.veryniche.stitchcounter.core.TrackedScreen
import dev.veryniche.stitchcounter.core.trackEvent
import dev.veryniche.stitchcounter.core.trackProjectScreenView
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.wear.getNextCounterId
import dev.veryniche.stitchcounter.wear.previews.PreviewScreen
import kotlinx.coroutines.launch

@Composable
fun ProjectScreen(
    id: Int,
    project: Project,
    listState: ScalingLazyListState,
    onCounterUpdate: (counter: Counter) -> Unit,
    onCounterClick: (counter: Counter) -> Unit,
    onProjectEdit: (projectId: Int, projectName: String) -> Unit,
    onCounterAdd: (counterId: Int) -> Unit,
    onProjectReset: (Project) -> Unit,
    keepScreenOn: Boolean,
    onKeepScreenOnUpdate: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    ambientAwareState: AmbientStateUpdate
) {
    val composableScope = rememberCoroutineScope()
    var showResetProjectDialog by remember { mutableStateOf(false) }
    TrackedScreen {
        trackProjectScreenView(project.counters.size, isMobile = false)
    }
    val nextCounterId = project.getNextCounterId()
    ProjectContent(
        project = project,
        listState = listState,
        onCounterUpdate = { counter ->
            onCounterUpdate.invoke(counter)
        },
        onCounterAdd = { onCounterAdd.invoke(nextCounterId) },
        onProjectReset = {
            showResetProjectDialog = true
        },
        onProjectEdit = { onProjectEdit.invoke(id, project.name) },
        onCounterClick = onCounterClick,
        ambientAwareState = ambientAwareState,
        keepScreenOn = keepScreenOn,
        onKeepScreenOnUpdate = onKeepScreenOnUpdate,
        modifier = modifier
    )
    if (showResetProjectDialog) {
        ResetProjectAlert(
            projectName = project.name,
            onConfirm = {
                composableScope.launch {
                    trackEvent(Analytics.Action.ResetProject, isMobile = false)
                    onProjectReset.invoke(project)
                    showResetProjectDialog = false
                }
            },
            onCancel = {
                showResetProjectDialog = false
            }
        )
    }
}

@OptIn(ExperimentalWearFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun ProjectContent(
    project: Project,
    listState: ScalingLazyListState,
    onCounterAdd: () -> Unit,
    onCounterUpdate: (counter: Counter) -> Unit,
    onCounterClick: (counter: Counter) -> Unit,
    onProjectReset: () -> Unit,
    onProjectEdit: () -> Unit,
    keepScreenOn: Boolean,
    onKeepScreenOnUpdate: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    ambientAwareState: AmbientStateUpdate
) {
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
        state = listState
    ) {
        item {
            ListHeader {
                ListTitle(
                    if (BuildConfig.SHOW_IDS) {
                        "${project.name} (${project.id})"
                    } else {
                        project.name
                    }
                )
            }
        }
        items(project.counters) { counter ->
            CounterListItemComponent(
                counter = counter,
                onCounterUpdate = onCounterUpdate,
                onCounterClick = onCounterClick,
                Modifier
            )
        }
        if (ambientAwareState.ambientState is AmbientState.Interactive) {
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    CompactButton(
                        onClick = { onCounterAdd.invoke() },
                        colors = ButtonDefaults.secondaryButtonColors()
                    ) {
                        Icon(
                            imageVector = Filled.Add,
                            contentDescription = stringResource(id = string.add_counter)
                        )
                    }
                    CompactButton(
                        onClick = {
                            onProjectReset.invoke()
                        },
                        colors = ButtonDefaults.secondaryButtonColors()
                    ) {
                        Icon(
                            imageVector = Filled.Refresh,
                            contentDescription = stringResource(id = string.reset_project)
                        )
                    }
                    CompactButton(
                        onClick = { onProjectEdit.invoke() },
                        colors = ButtonDefaults.secondaryButtonColors()
                    ) {
                        Icon(
                            imageVector = Filled.Edit,
                            contentDescription = stringResource(id = string.edit_project)
                        )
                    }
                    var keepScreenOnState by remember { mutableStateOf(keepScreenOn) }
                    CompactButton(
                        onClick = {
                            keepScreenOnState = !keepScreenOnState
                            onKeepScreenOnUpdate.invoke(keepScreenOnState)
                        },
                        colors = ButtonDefaults.secondaryButtonColors()
                    ) {
                        Icon(
                            imageVector = if (keepScreenOnState) {
                                Filled.BrightnessHigh
                            } else {
                                Filled.BrightnessLow
                            },
                            contentDescription = stringResource(id = string.keep_screen_on_toggle)
                        )
                    }
                }
            }
        }
        item {
            Text(
                modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp).alpha(0.5f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.caption3,
                color = MaterialTheme.colors.onBackground,
                text = stringResource(R.string.project_last_updated, project.lastModifiedString)
            )
        }
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ResetProjectAlert(projectName: String, onConfirm: () -> Unit, onCancel: () -> Unit) {
    ResponsiveDialogContent(
        title = {
            Text(
                text = stringResource(string.reset_project_message, projectName),
                textAlign = TextAlign.Center
            )
        },
        onOk = onConfirm,
        onCancel = onCancel,
        okButtonContentDescription = stringResource(string.reset_project_positive),
        cancelButtonContentDescription = stringResource(string.reset_project_negative),
        showPositionIndicator = true,
        modifier = Modifier.background(MaterialTheme.colors.background),
    )
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
        onCounterAdd = {},
        onCounterUpdate = {},
        onCounterClick = {},
        onProjectReset = {},
        onProjectEdit = {},
        keepScreenOn = true,
        onKeepScreenOnUpdate = {},
        modifier = Modifier.fillMaxSize(),
        ambientAwareState = AmbientStateUpdate(ambientState = AmbientState.Interactive)
    )
}

@PreviewScreen
@Composable
fun ProjectContentEmptyPreview() {
    ProjectContent(
        project = Project(
            id = 0,
            name = "shawl",
            counters = listOf()
        ),
        listState = rememberScalingLazyListState(),
        onCounterAdd = {},
        onCounterUpdate = {},
        onCounterClick = {},
        onProjectReset = {},
        onProjectEdit = {},
        keepScreenOn = true,
        onKeepScreenOnUpdate = {},
        modifier = Modifier.fillMaxSize(),
        ambientAwareState = AmbientStateUpdate(ambientState = AmbientState.Interactive)
    )
}

@PreviewScreen
@Composable
fun ProjectContentAmbientPreview() {
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
        onCounterAdd = {},
        onCounterUpdate = {},
        onCounterClick = {},
        onProjectReset = {},
        onProjectEdit = {},
        keepScreenOn = true,
        onKeepScreenOnUpdate = {},
        modifier = Modifier.fillMaxSize(),
        ambientAwareState = AmbientStateUpdate(ambientState = AmbientState.Ambient())
    )
}
