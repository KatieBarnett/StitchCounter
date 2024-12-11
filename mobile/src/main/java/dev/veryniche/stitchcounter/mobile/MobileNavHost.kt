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
                        navController.navigate("about")
                    },
                    onOpenCounter = {
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

//        composable("counter/{project_id}/{counter_id}") {
//            val projectId = it.arguments?.getString("project_id")?.toIntOrNull()
//            val counterId = it.arguments?.getString("counter_id")?.toIntOrNull()
//            if (projectId != null && counterId != null) {
//                viewModel.updatePageContext(stringResource(id = R.string.context_counter))
//                CounterScreen(
//                    viewModel = viewModel,
//                    projectId = projectId,
//                    counterId = counterId,
//                    onCounterEdit = { counterName, counterMax ->
//                        navController.navigate("edit_counter/$projectId/$counterId/$counterName/$counterMax")
//                    }
//                )
//            }
//        }
//        composable("edit_counter/{project_id}/{counter_id}") {
//            val projectId = it.arguments?.getString("project_id")?.toIntOrNull()
//            val counterId = it.arguments?.getString("counter_id")?.toIntOrNull()
//            if (projectId != null && counterId != null) {
//                viewModel.updatePageContext(stringResource(id = R.string.context_counter))
//                LoadEditCounterScreen(
//                    navController = navController,
//                    viewModel = viewModel,
//                    projectId = projectId,
//                    counterId = counterId,
//                    counterName = null,
//                    counterMax = 0
//                )
//            }
//        }
//        composable("edit_counter/{project_id}/{counter_id}/{counter_name}/{counter_max}") {
//            val projectId = it.arguments?.getString("project_id")?.toIntOrNull()
//            val counterId = it.arguments?.getString("counter_id")?.toIntOrNull()
//            val counterName = it.arguments?.getString("counter_name")
//            val counterMax = it.arguments?.getString("counter_max")?.toIntOrNull() ?: 0
//            if (projectId != null && counterId != null) {
//                viewModel.updatePageContext(stringResource(id = R.string.context_counter))
//                LoadEditCounterScreen(
//                    navController = navController,
//                    viewModel = viewModel,
//                    projectId = projectId,
//                    counterId = counterId,
//                    counterName = counterName,
//                    counterMax = counterMax
//                )
//            }
//        }
    }
}
//
// @Composable
// fun LoadEditCounterScreen(
//    navController: NavHostController,
//    viewModel: MainViewModel,
//    projectId: Int,
//    counterId: Int,
//    counterName: String?,
//    counterMax: Int,
// ) {
//    val composableScope = rememberCoroutineScope()
//    EditCounterScreen(
//        counterId = counterId,
//        initialName = counterName,
//        initialMax = counterMax,
//        onSave = { counterName, counterMax ->
//            composableScope.launch {
//                viewModel.saveCounter(projectId, counterId, counterName, counterMax)
//                navController.navigateUp()
//            }
//        },
//        onDelete = {
//            composableScope.launch {
//                trackEvent(Analytics.Action.DeleteCounter)
//                viewModel.deleteCounter(projectId, counterId)
//                navController.navigateUp()
//            }
//        },
//        onClose = {
//            navController.navigateUp()
//        }
//    )
// }
