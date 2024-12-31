package dev.veryniche.stitchcounter.mobile.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
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
import dev.veryniche.stitchcounter.mobile.components.CollapsedTopAppBar
import dev.veryniche.stitchcounter.mobile.components.ExpandingTopAppBar
import dev.veryniche.stitchcounter.mobile.components.ProjectItem
import dev.veryniche.stitchcounter.mobile.components.SettingsActionIcon
import dev.veryniche.stitchcounter.mobile.previews.PreviewScreen
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@Composable
fun ProjectListScreen(
    viewModel: MainViewModel,
    onProjectClick: (Int) -> Unit,
    onAddProjectClick: () -> Unit,
    onAboutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
) {
    val projects: List<Project> by viewModel.projects.collectAsState(initial = emptyList())
    ProjectList(
        projects.sortedByDescending {
            it.lastModified
        },
        onProjectClick,
        onAddProjectClick,
        onAboutClick,
        onSettingsClick,
        snackbarHostState,
        modifier,
        windowSizeClass
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectList(
    projectList: List<Project>,
    onProjectClick: (Int) -> Unit,
    onAddProjectClick: () -> Unit,
    onAboutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
) {
    TrackedScreen {
        trackScreenView(name = Screen.ProjectList, isMobile = true)
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            if (windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT) {
                CollapsedTopAppBar(
                    titleText = stringResource(id = R.string.app_name),
                    actions = {
                        SettingsActionIcon { onSettingsClick.invoke() }
                        AboutActionIcon { onAboutClick.invoke() }
                    },
                    navigationIcon = {}
                )
            } else {
                ExpandingTopAppBar(
                    titleText = stringResource(id = R.string.app_name),
                    actions = {
                        SettingsActionIcon { onSettingsClick.invoke() }
                        AboutActionIcon { onAboutClick.invoke() }
                    },
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {}
                )
            }
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
                LazyVerticalGrid(
                    verticalArrangement = Arrangement.spacedBy(Dimen.spacingQuad),
                    horizontalArrangement = Arrangement.spacedBy(Dimen.spacingQuad),
                    contentPadding = PaddingValues(Dimen.spacingQuad),
                    columns = Fixed(
                        when (windowSizeClass.windowWidthSizeClass) {
                            WindowWidthSizeClass.EXPANDED -> 3
                            WindowWidthSizeClass.MEDIUM -> 2
                            else -> 1
                        }
                    ),
                    modifier = modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(projectList) { project ->
                        ProjectItem(project, onProjectClick, Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@PreviewScreen
@Composable
fun ProjectListEmptyPreview() {
    StitchCounterTheme {
        ProjectList(
            projectList = listOf(),
            onProjectClick = {},
            onAddProjectClick = {},
            onSettingsClick = {},
            onAboutClick = {},
            modifier = Modifier
        )
    }
}

@PreviewScreen
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
            onSettingsClick = {},
            onAddProjectClick = {},
            onAboutClick = {},
            modifier = Modifier
        )
    }
}
