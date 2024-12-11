package dev.veryniche.stitchcounter.mobile.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import dev.veryniche.stitchcounter.core.Analytics.Screen
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.core.TrackedScreen
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.core.trackScreenView
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.mobile.MainViewModel
import dev.veryniche.stitchcounter.mobile.ads.BannerAd
import dev.veryniche.stitchcounter.mobile.ads.BannerAdLocation
import dev.veryniche.stitchcounter.mobile.components.AboutActionIcon
import dev.veryniche.stitchcounter.mobile.components.ExpandingTopAppBar
import dev.veryniche.stitchcounter.mobile.components.ProjectItem
import dev.veryniche.stitchcounter.mobile.previews.PreviewComponent
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@Composable
fun ProjectListScreen(
    viewModel: MainViewModel,
    onProjectClick: (Int) -> Unit,
    onAddProjectClick: () -> Unit,
    onAboutClick: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier
) {
    val projects: List<Project> by viewModel.projects.collectAsState(initial = emptyList())
    ProjectList(projects.sortedByDescending {
        it.lastModified
    }, onProjectClick, onAddProjectClick, onAboutClick, snackbarHostState, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectList(
    projectList: List<Project>,
    onProjectClick: (Int) -> Unit,
    onAddProjectClick: () -> Unit,
    onAboutClick: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier
) {
    TrackedScreen {
        trackScreenView(name = Screen.ProjectList, isMobile = true)
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
            ExtendedFloatingActionButton(
                onClick = { onAddProjectClick.invoke() },
                icon = { Icon(Icons.Filled.Add, stringResource(id = R.string.add_project)) },
                text = { Text(text = stringResource(id = R.string.add_project)) },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            if (!LocalInspectionMode.current) {
                BannerAd(
                    location = BannerAdLocation.MainScreen,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        Column(Modifier.padding(contentPadding)) {
            if (projectList.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = Dimen.spacingQuad)
                ) {
                    Text(
                        text = stringResource(id = dev.veryniche.stitchcounter.mobile.R.string.project_list_empty),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Dimen.spacingQuad),
                    modifier = modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = Dimen.spacingQuad)
                ) {
                    item {
                        // Spacing
                    }
                    items(projectList) { project ->
                        ProjectItem(project, onProjectClick, Modifier.fillMaxWidth())
                    }
                    item {
                        // Spacing
                    }
                }
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
