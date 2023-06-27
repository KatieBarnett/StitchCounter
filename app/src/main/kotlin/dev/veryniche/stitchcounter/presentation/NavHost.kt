package dev.veryniche.stitchcounter.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import dev.veryniche.stitchcounter.MainViewModel
import dev.veryniche.stitchcounter.R
import dev.veryniche.stitchcounter.util.Analytics
import dev.veryniche.stitchcounter.util.trackEvent
import kotlinx.coroutines.launch

@Composable
fun NavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    listState: ScalingLazyListState
) {
    SwipeDismissableNavHost(
        navController = navController,
        startDestination = "project_list",
        modifier = modifier
    ) {
        composable("about") {
            viewModel.updatePageContext(stringResource(id = R.string.app_name))
            AboutScreen(listState = listState)
        }
        composable("project_list") {
            viewModel.updatePageContext(stringResource(id = R.string.app_name))
            ProjectListScreen(
                viewModel = viewModel,
                listState = listState,
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
                viewModel.updatePageContext(stringResource(id = R.string.project_title))
                ProjectScreen(
                    viewModel = viewModel,
                    id = projectId,
                    listState = listState,
                    onCounterClick = { counter ->
                        navController.navigate("counter/$projectId/${counter.id}")
                    },
                    onCounterAdd = { newCounterId ->
                        navController.navigate("edit_counter/$projectId/$newCounterId")
                    },
                    onProjectEdit = { id, name ->
                        navController.navigate("edit_project/$id/$name")
                    }
                )
            }
        }
        composable("edit_project") {
            viewModel.updatePageContext(stringResource(id = R.string.add_project))
            LoadEditProjectScreen(
                navController = navController,
                viewModel = viewModel,
                projectId = null,
                projectName = null
            )
        }
        composable("edit_project/{id}/{name}") {
            val projectId = it.arguments?.getString("id")?.toIntOrNull()
            val projectName = it.arguments?.getString("name")
            if (projectId != null && projectName != null) {
                viewModel.updatePageContext(stringResource(id = R.string.edit_project))
                LoadEditProjectScreen(
                    navController = navController,
                    viewModel = viewModel,
                    projectId = projectId,
                    projectName = projectName
                )
            }
        }
        composable("counter/{project_id}/{counter_id}") {
            val projectId = it.arguments?.getString("project_id")?.toIntOrNull()
            val counterId = it.arguments?.getString("counter_id")?.toIntOrNull()
            if (projectId != null && counterId != null) {
                viewModel.updatePageContext(stringResource(id = R.string.counter_title))
                CounterScreen(
                    viewModel = viewModel,
                    projectId = projectId,
                    counterId = counterId,
                    onCounterEdit = { counterName, counterMax ->
                        navController.navigate("edit_counter/$projectId/$counterId/$counterName/$counterMax")
                    }
                )
            }
        }
        composable("edit_counter/{project_id}/{counter_id}") {
            val projectId = it.arguments?.getString("project_id")?.toIntOrNull()
            val counterId = it.arguments?.getString("counter_id")?.toIntOrNull()
            if (projectId != null && counterId != null) {
                viewModel.updatePageContext(stringResource(id = R.string.add_counter))
                LoadEditCounterScreen(
                    navController = navController,
                    viewModel = viewModel,
                    projectId = projectId,
                    counterId = counterId,
                    counterName = null,
                    counterMax = 0
                )
            }
        }
        composable("edit_counter/{project_id}/{counter_id}/{counter_name}/{counter_max}") {
            val projectId = it.arguments?.getString("project_id")?.toIntOrNull()
            val counterId = it.arguments?.getString("counter_id")?.toIntOrNull()
            val counterName = it.arguments?.getString("counter_name")
            val counterMax = it.arguments?.getString("counter_max")?.toIntOrNull() ?: 0
            if (projectId != null && counterId != null) {
                viewModel.updatePageContext(stringResource(id = R.string.edit_counter))
                LoadEditCounterScreen(
                    navController = navController,
                    viewModel = viewModel,
                    projectId = projectId,
                    counterId = counterId,
                    counterName = counterName,
                    counterMax = counterMax
                )
            } 
        }
    }
}

@Composable
fun LoadEditProjectScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    projectId: Int?,
    projectName: String?
) {
    val composableScope = rememberCoroutineScope()
    EditProjectScreen(
        initialName = projectName,
        onSave = { projectName ->
            composableScope.launch {
                viewModel.saveProject(projectId, projectName)
                navController.navigateUp()
            }
        },
        onDelete = {
            composableScope.launch {
                projectId?.let {
                    trackEvent(Analytics.Action.DeleteProject)
                    viewModel.deleteProject(projectId)
                }
                navController.navigateUp()
            }
        },
        onClose = {
            navController.navigateUp()
        }
    )
}

@Composable
fun LoadEditCounterScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    projectId: Int,
    counterId: Int,
    counterName: String?,
    counterMax: Int,
) {
    val composableScope = rememberCoroutineScope()
    EditCounterScreen(
        counterId = counterId,
        initialName = counterName,
        initialMax = counterMax,
        onSave = { counterName, counterMax ->
            composableScope.launch {
                viewModel.saveCounter(projectId, counterId, counterName, counterMax)
                navController.navigateUp()
            }
        },
        onDelete = {
            composableScope.launch {
                trackEvent(Analytics.Action.DeleteCounter)
                viewModel.deleteCounter(projectId, counterId)
                navController.navigateUp()
            }
        },
        onClose = {
            navController.navigateUp()
        }
    )
}