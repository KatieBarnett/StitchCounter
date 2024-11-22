package dev.veryniche.stitchcounter.mobile

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
    NavHost(
        navController = navController,
        startDestination = "project_list",
        modifier = modifier
    ) {
        composable("about") {
            AboutScreen(
                purchaseStatus = purchaseStatus,
                snackbarHostState = snackbarHostState,
                onNavigateBack = { navController.navigateUp() },
                onPurchaseClick = onPurchaseClick
            )
        }
        composable("project_list") {
            ProjectListScreen(
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
                onProjectClick = { projectId ->
                    navController.navigate("project/$projectId")
                },
                onAddProjectClick = {
                    navController.navigate("project")
                },
                onAboutClick = {
                    navController.navigate("about")
                }
            )
        }
        composable("project") {
            val composableScope = rememberCoroutineScope()
            val defaultProjectName = stringResource(R.string.project_name_default)
            var projectState by remember { mutableStateOf(Project(name = defaultProjectName)) }
            ProjectScreen(
                project = projectState,
                snackbarHostState = snackbarHostState,
                projectEditMode = true,
                onSave = { updatedProject ->
                    composableScope.launch {
                        viewModel.saveProject(updatedProject)
                        projectState = updatedProject
                    }
                },
                onDelete = {
                    composableScope.launch {
                        projectState.id?.let {
                            trackEvent(Action.DeleteProject, isMobile = true)
                            viewModel.deleteProject(it)
                        }
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
                onCounterUpdate = {

                },
                onCounterDelete = {

                }
            )
        }
        composable("project/{id}") {
            it.arguments?.getString("id")?.toIntOrNull()?.let { projectId ->
                val projectState = viewModel.getProject(projectId).collectAsState(initial = null)
                projectState.value?.let { project ->
                    val composableScope = rememberCoroutineScope()
                    ProjectScreen(
                        project = project,
                        snackbarHostState = snackbarHostState,
                        onSave = { updatedProject ->
                            composableScope.launch {
                                viewModel.saveProject(updatedProject)
                            }
                        },
                        onDelete = {
                            composableScope.launch {
                                trackEvent(Action.DeleteProject, isMobile = true)
                                viewModel.deleteProject(projectId)
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
                        onCounterDelete = {

                        },
                        onCounterUpdate = {

                        },
                    )
                }
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
