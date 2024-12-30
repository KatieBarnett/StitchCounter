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
                purchaseStatus = purchaseStatus,
                snackbarHostState = snackbarHostState,
                onNavigateBack = { navController.navigateUp() },
                onPurchaseClick = onPurchaseClick
            )
        }
        composable<ProjectListDestination> {
            val coroutineScope = rememberCoroutineScope()
            ProjectListScreen(
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
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
                }
            )
        }
        composable<ProjectDestination> { backstackNavigation ->
            val arguments = backstackNavigation.toRoute<ProjectDestination>()
            val coroutineScope = rememberCoroutineScope()
            val projectState by viewModel.getProject(arguments.id).collectAsState(null)
            projectState?.let { project ->
                ProjectScreen(
                    project = project,
                    snackbarHostState = snackbarHostState,
                    projectEditMode = arguments.inEditMode,
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
                    onAboutClick = {
                        navController.navigate(AboutDestination)
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
                    }
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
                    snackbarHostState = snackbarHostState,
                    initialInEditMode = false,
                    onBack = {
                        navController.navigateUp()
                    },
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
                    }
                )
            }
        }
    }
}
