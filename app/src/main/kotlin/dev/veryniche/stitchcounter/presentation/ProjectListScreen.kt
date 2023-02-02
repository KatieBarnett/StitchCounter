package dev.veryniche.stitchcounter.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import dev.veryniche.stitchcounter.util.Analytics
import dev.veryniche.stitchcounter.util.TrackedScreen
import dev.veryniche.stitchcounter.util.trackScreenView
import dev.veryniche.stitchcounter.MainViewModel
import dev.veryniche.stitchcounter.R
import dev.veryniche.stitchcounter.R.string
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.presentation.theme.Dimen

@Composable
fun ProjectListScreen(
    viewModel: MainViewModel,
    listState: ScalingLazyListState,
    onProjectClick: (Int) -> Unit,
    onAddProjectClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val projects: List<Project> by viewModel.projects.collectAsState(initial = emptyList())
    ProjectList(projects, listState,  onProjectClick, onAddProjectClick, onAboutClick, modifier)
}

@Composable
fun ProjectList(
    projectList: List<Project>,
    listState: ScalingLazyListState,
    onProjectClick: (Int) -> Unit,
    onAddProjectClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TrackedScreen {
        trackScreenView(name = Analytics.Screen.ProjectList)
    }
    ScalingLazyColumn (
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .selectableGroup(),
        state = listState,
    ) {
        item {
            ListHeader(Modifier) {
                ListTitle(stringResource(string.title), modifier = Modifier.padding(top = Dimen.spacingQuad))
            }
        }
        items (projectList) { project ->
            ProjectChip(project, onProjectClick)
        }
        item {
            Row(horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(0.75f)) {
                AddProjectButton(onAddProjectClick)
                AboutButton(onAboutClick)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProjectChip(project: Project, onProjectClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    Chip(colors = ChipDefaults.primaryChipColors(),
        modifier = modifier.fillMaxWidth(),
        label = {
            Text(
                text = project.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        secondaryLabel = {
            Text(
                text = if (project.counters.isEmpty()) {
                    stringResource(R.string.counters_label_zero)
                } else {
                    pluralStringResource(R.plurals.counters_label, project.counters.size, project.counters.size)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        onClick = {
            project.id?.let {
                onProjectClick.invoke(it)
            }
        }
    )
}



@Composable
fun AddProjectButton(onAddProjectClick: () -> Unit, modifier: Modifier = Modifier) {
    CompactButton(onClick = onAddProjectClick,
        colors = ButtonDefaults.secondaryButtonColors(),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(id = R.string.add_project)
        )
    }
}

@Composable
fun AboutButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    CompactButton(onClick = onClick,
        colors = ButtonDefaults.secondaryButtonColors(),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = stringResource(id = R.string.about_title)
        )
    }
}


