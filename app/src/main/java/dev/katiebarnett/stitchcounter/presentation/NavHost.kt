package dev.katiebarnett.stitchcounter.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import dev.katiebarnett.stitchcounter.MainViewModel

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
            ProjectListScreen(
                viewModel = viewModel,
                listState = listState,
                onProjectClick = { id ->
                    navController.navigate("project/$id")
                },
                onAddProjectClick = {
                    navController.navigate("add_project")
                }
            )
        }
        composable("add_project") {
            AddProjectScreen(onComplete = {
                navController.navigateUp()
            })
        }
        composable("project/{id}") {
            it.arguments?.getInt("id")?.let {
                ProjectScreen(viewModel = viewModel, id = it, listState = listState)
            }
        }
    }
}