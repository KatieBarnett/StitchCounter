package dev.veryniche.stitchcounter.mobile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import dev.veryniche.stitchcounter.core.AnalyticsConstants
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.mobile.MainViewModel
import dev.veryniche.stitchcounter.mobile.TrackedScreen
import dev.veryniche.stitchcounter.mobile.components.AboutActionIcon
import dev.veryniche.stitchcounter.mobile.components.ExpandingTopAppBar
import dev.veryniche.stitchcounter.mobile.components.ProjectItem
import dev.veryniche.stitchcounter.mobile.previews.PreviewComponent
import dev.veryniche.stitchcounter.mobile.trackScreenView
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@Composable
fun ProjectListScreen(
    viewModel: MainViewModel,
    onProjectClick: (Int) -> Unit,
    onAddProjectClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val projects: List<Project> by viewModel.projects.collectAsState(initial = emptyList())
    ProjectList(projects, onProjectClick, onAddProjectClick, onAboutClick, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectList(
    projectList: List<Project>,
    onProjectClick: (Int) -> Unit,
    onAddProjectClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TrackedScreen {
        trackScreenView(name = AnalyticsConstants.Screen.ProjectList)
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            ExpandingTopAppBar(
                titleText = stringResource(id = R.string.app_name),
                actions = {
                    AboutActionIcon { onAboutClick.invoke() }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddProjectClick.invoke() },
            ) {
                Icon(Icons.Filled.Add, stringResource(id = R.string.add_project))
            }
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Dimen.spacingQuad),
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    top = contentPadding.calculateTopPadding() + Dimen.spacingQuad,
                    start = contentPadding.calculateStartPadding(LayoutDirection.Ltr) + Dimen.spacingQuad,
                    end = contentPadding.calculateEndPadding(LayoutDirection.Ltr) + Dimen.spacingQuad,
                    bottom = contentPadding.calculateBottomPadding()
                )
        ) {
            items(projectList) { project ->
                ProjectItem(project, onProjectClick, Modifier.fillMaxWidth())
            }
        }
    }
}

@PreviewComponent
@Composable
fun ProjectListEmptyPreview() {
    StitchCounterTheme {
        ProjectList(
            projectList = listOf(),
            onProjectClick = {},
            onAddProjectClick = {},
            onAboutClick = {},
            modifier = Modifier
        )
    }
}

@PreviewComponent
@Composable
fun ProjectListPreview() {
    StitchCounterTheme {
        ProjectList(
            projectList = listOf(
                Project(
                    name = "Project 1",
                    counters = listOf(),
                ),
                Project(
                    name = "Project 2",
                    counters = listOf(
                        Counter(id = 0, name = "Counter 1"),
                        Counter(id = 1, name = "Counter 2")
                    ),
                )
            ),
            onProjectClick = {},
            onAddProjectClick = {},
            onAboutClick = {},
            modifier = Modifier
        )
    }
}
