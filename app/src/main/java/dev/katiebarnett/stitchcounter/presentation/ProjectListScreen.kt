package dev.katiebarnett.stitchcounter.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.items
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import dev.katiebarnett.stitchcounter.R
import dev.katiebarnett.stitchcounter.MainViewModel
import dev.katiebarnett.stitchcounter.models.Project

@Composable
fun ProjectListScreen(
    viewModel: MainViewModel,
    listState: ScalingLazyListState,
    onProjectClick: (Int) -> Unit,
    onAddProjectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProjectList(viewModel.projects, listState,  onProjectClick, onAddProjectClick, modifier)
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
            Title()
        }
        items (projectList) { project ->
            Chip(
                modifier = Modifier.fillMaxWidth(),
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
                onClick = { onProjectClick(project.id) }
            )
        }
        item {
            Button(onClick = { onAddProjectClick.invoke() },
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.add_project)
                )
            }
        }
    }
}

@Composable
fun Title() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.title)
    )
}
