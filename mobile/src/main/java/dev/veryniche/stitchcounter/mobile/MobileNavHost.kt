package dev.veryniche.stitchcounter.mobile

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.window.core.layout.WindowSizeClass
import dev.veryniche.stitchcounter.core.Analytics.Action
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.core.trackEvent
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseAction
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseStatus
import dev.veryniche.stitchcounter.mobile.screens.AboutScreen
import dev.veryniche.stitchcounter.mobile.screens.CounterScreen
import dev.veryniche.stitchcounter.mobile.screens.ProjectListScreen
import dev.veryniche.stitchcounter.mobile.screens.ProjectScreen
import kotlinx.coroutines.launch

@Composable
fun MobileNavHost(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: MainViewModel,
    purchaseStatus: PurchaseStatus,
    onPurchaseClick: (PurchaseAction) -> Unit,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
) {
    val defaultProjectName = stringResource(R.string.project_name_default)
    NavHost(
        navController = navController,
        startDestination = ProjectListDestination,
        modifier = modifier
    ) {
        composable<AboutDestination> {
            AboutScreen(
                onNavigateBack = { navController.navigateUp() },
                purchaseStatus = purchaseStatus,
                onPurchaseClick = onPurchaseClick,
                snackbarHostState = snackbarHostState,
                windowSizeClass = windowSizeClass,
            )
        }
        composable<ProjectListDestination> {
            val coroutineScope = rememberCoroutineScope()
            ProjectListScreen(
                viewModel = viewModel,
                onProjectClick = { projectId ->
                    navController.navigate(ProjectDestination(projectId, false))
                },
                onAddProjectClick = {
                    coroutineScope.launch {
                        val newProjectId = viewModel.saveProject(Project(name = defaultProjectName))
                        navController.navigate(ProjectDestination(newProjectId, true))
                    }
                },
                onAboutClick = {
                    navController.navigate(AboutDestination)
                },
                snackbarHostState = snackbarHostState,
                windowSizeClass = windowSizeClass,
            )
        }
        composable<ProjectDestination> { backstackNavigation ->
            val arguments = backstackNavigation.toRoute<ProjectDestination>()
            val coroutineScope = rememberCoroutineScope()
            val projectState by viewModel.getProject(arguments.id).collectAsState(null)
            projectState?.let { project ->
                ProjectScreen(
                    project = project,
                    projectEditMode = arguments.inEditMode,
                    onAboutClick = {
                        navController.navigate(AboutDestination)
                    },
                    onSave = { updatedProject ->
                        coroutineScope.launch {
                            viewModel.saveProject(updatedProject)
                        }
                    },
                    onDelete = {
                        coroutineScope.launch {
                            trackEvent(Action.DeleteProject, isMobile = true)
                            viewModel.deleteProject(arguments.id)
                            navController.navigateUp()
                        }
                    },
                    onBack = {
                        navController.navigateUp()
                    },
                    onOpenCounter = { counter ->
                        projectState?.id?.let {
                            navController.navigate(CounterDestination(it, counter.id))
                        }
                    },
                    onCounterUpdate = { updatedCounter ->
                        coroutineScope.launch {
                            viewModel.updateCounter(project, updatedCounter)
                        }
                    },
                    onCounterDelete = { counter ->
                        coroutineScope.launch {
                            viewModel.deleteCounter(project.id ?: -1, counter.id)
                        }
                    },
                    snackbarHostState = snackbarHostState,
                    windowSizeClass = windowSizeClass,
                )
            }
        }
        composable<CounterDestination> { backstackNavigation ->
            val arguments = backstackNavigation.toRoute<CounterDestination>()
            val coroutineScope = rememberCoroutineScope()
            val projectState by viewModel.getProject(arguments.projectId).collectAsState(null)
            projectState?.counters?.firstOrNull { it.id == arguments.counterId }?.let { counter ->
                CounterScreen(
                    counter = counter,
                    onAboutClick = {
                        navController.navigate(AboutDestination)
                    },
                    onCounterUpdate = { updatedCounter ->
                        coroutineScope.launch {
                            projectState?.let {
                                viewModel.updateCounter(it, updatedCounter)
                            }
                        }
                    },
                    onCounterDelete = {
                        coroutineScope.launch {
                            viewModel.deleteCounter(arguments.projectId, counter.id)
                            navController.navigateUp()
                        }
                    },
                    onBack = {
                        navController.navigateUp()
                    },
                    snackbarHostState = snackbarHostState,
                    windowSizeClass = windowSizeClass,
                )
            }
        }
    }
}
