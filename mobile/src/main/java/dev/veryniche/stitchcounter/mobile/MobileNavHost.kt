package dev.veryniche.stitchcounter.mobile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.veryniche.stitchcounter.core.AnalyticsConstants
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseAction
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseStatus
import dev.veryniche.stitchcounter.mobile.screens.AboutScreen
import dev.veryniche.stitchcounter.mobile.screens.ProjectListScreen
import dev.veryniche.stitchcounter.mobile.screens.ProjectScreen
import kotlinx.coroutines.launch

@Composable
fun MobileNavHost(
    navController: NavHostController,
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
                onNavigateBack = { navController.navigateUp() },
                onPurchaseClick = onPurchaseClick
            )
        }
        composable("project_list") {
            ProjectListScreen(
                viewModel = viewModel,
                onProjectClick = { projectId ->
                    navController.navigate("project/$projectId")
                },
                onAddProjectClick = {
                    navController.navigate("edit_project")
                },
                onAboutClick = {
                    navController.navigate("about")
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
                        onSave = { updatedProject ->
                            composableScope.launch {
                                viewModel.saveProject(updatedProject)
                                navController.navigateUp()
                            }
                        },
                        onDelete = {
                            composableScope.launch {
                                trackEvent(AnalyticsConstants.Action.DeleteProject)
                                viewModel.deleteProject(projectId)
                                navController.navigateUp()
                            }
                        },
                        onBack = {
                            navController.navigateUp()
                        },
                        onAboutClick = {
                            navController.navigate("about")
                        }
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
