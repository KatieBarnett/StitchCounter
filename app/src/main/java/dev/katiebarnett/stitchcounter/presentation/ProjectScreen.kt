package dev.katiebarnett.stitchcounter.presentation

import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender
import dev.katiebarnett.stitchcounter.R
import dev.katiebarnett.stitchcounter.MainViewModel
import dev.katiebarnett.stitchcounter.models.Counter
import dev.katiebarnett.stitchcounter.models.Project
import dev.katiebarnett.stitchcounter.presentation.theme.Dimen

@Composable
fun ProjectScreen(id: Int, viewModel: MainViewModel, listState: ScalingLazyListState, modifier: Modifier = Modifier) {
    val project = viewModel.getProject(id)
    if (project != null) {
        ProjectContent(
            project = project,
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