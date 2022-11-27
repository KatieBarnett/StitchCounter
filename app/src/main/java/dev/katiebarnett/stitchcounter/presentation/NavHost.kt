package dev.katiebarnett.stitchcounter.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import dev.katiebarnett.stitchcounter.MainViewModel
import dev.katiebarnett.stitchcounter.R

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
                }
            )
        }
        composable("edit_project") {
            viewModel.updatePageContext(stringResource(id = R.string.add_project))
            EditProjectScreen(
                onSave = { projectName ->
                    viewModel.saveProject(projectName)
                    navController.navigateUp()
                },
                onDelete = {
                    navController.navigateUp()
                },
                onClose = {
                    navController.navigateUp()
                }
            )
        }
        composable("edit_project/{id}/{name}") {
            val projectId = it.arguments?.getString("id")?.toIntOrNull()
            val projectName = it.arguments?.getString("name")
            if (projectId != null && projectName != null) {
                viewModel.updatePageContext(stringResource(id = R.string.edit_project))
                EditProjectScreen(
                    initialName = projectName, 
                    onSave = { project ->
                        viewModel.saveProject(projectId, projectName)
                        navController.navigateUp()
                    },
                    onDelete = {
                        viewModel.deleteProject(projectId)
                        navController.navigateUp()
                    },
                    onClose = {
                        navController.navigateUp()
                    }
                )
            }
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
        composable("counter/{project_id}/{counter_id}") {
            val projectId = it.arguments?.getString("project_id")?.toIntOrNull()
            val counterId = it.arguments?.getString("counter_id")?.toIntOrNull()
            if (projectId != null && counterId != null) {
                viewModel.updatePageContext(stringResource(id = R.string.counter_title))
                CounterScreen(
                    viewModel = viewModel,
                    projectId = projectId,
                    counterId = counterId,
                    onCounterEdit = { counterName ->
                        navController.navigate("edit_counter/$projectId/$counterId/$counterName")
                    }
                )
            }
        }
        composable("edit_counter/{project_id}/{counter_id}") {
            val projectId = it.arguments?.getString("project_id")?.toIntOrNull()
            val counterId = it.arguments?.getString("counter_id")?.toIntOrNull()
            if (projectId != null && counterId != null) {
                viewModel.updatePageContext(stringResource(id = R.string.add_counter))
                EditCounterScreen(
                    counterId = counterId,
                    initialName = null,
                    onSave = { counterName, counterMax ->
                        viewModel.saveCounter(projectId, counterId, counterName, counterMax)
                        navController.navigateUp()
                    },
                    onDelete = {
                        viewModel.deleteProject(projectId)
                        navController.navigateUp()
                    },
                    onClose = {
                        navController.navigateUp()
                    }
                )
            }
        }
        composable("edit_counter/{project_id}/{counter_id}/{counter_name}") {
            val projectId = it.arguments?.getString("project_id")?.toIntOrNull()
            val counterId = it.arguments?.getString("counter_id")?.toIntOrNull()
            val counterName = it.arguments?.getString("counter_name")
            if (projectId != null && counterId != null) {
                viewModel.updatePageContext(stringResource(id = R.string.edit_counter))
                EditCounterScreen(
                    counterId = counterId,
                    initialName = counterName,
                    onSave = { counterName, counterMax ->
                        viewModel.saveCounter(projectId, counterId, counterName, counterMax)
                        navController.navigateUp()
                    },
                    onDelete = {
                        viewModel.deleteProject(projectId)
                        navController.navigateUp()
                    },
                    onClose = {
                        navController.navigateUp()
                    }
                )
            } 
        }
    }
}