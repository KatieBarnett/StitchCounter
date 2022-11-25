package dev.katiebarnett.stitchcounter.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.AutoCenteringParams
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
import dev.katiebarnett.stitchcounter.MainViewModel
import dev.katiebarnett.stitchcounter.R
import dev.katiebarnett.stitchcounter.R.string
import dev.katiebarnett.stitchcounter.data.models.Project

@Composable
fun ProjectListScreen(
    viewModel: MainViewModel,
    listState: ScalingLazyListState,
    onProjectClick: (Int) -> Unit,
    onAddProjectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val projects: List<Project> by viewModel.projects.collectAsState(initial = emptyList())
    ProjectList(projects, listState,  onProjectClick, onAddProjectClick, modifier)
}

@Composable
fun ProjectList(
    projectList: List<Project>,
    listState: ScalingLazyListState,
    onProjectClick: (Int) -> Unit,
    onAddProjectClick: () -> Unit,
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
            ListHeader() {
                ListTitle(stringResource(string.title))
            }
        }
        items (projectList) { project ->
            ProjectChip(project, onProjectClick)
        }
        item {
            AddProjectButton(onAddProjectClick)
        }
    }
}

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
                text = "${project.counters.size} counters",
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
    CompactButton(onClick = { onAddProjectClick.invoke() },
        colors = ButtonDefaults.secondaryButtonColors(),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(id = R.string.add_project)
        )
    }
}


