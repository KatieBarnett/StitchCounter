package dev.veryniche.stitchcounter.wear.presentation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import com.google.android.horologist.compose.ambient.AmbientStateUpdate
import dev.veryniche.stitchcounter.core.Analytics
import dev.veryniche.stitchcounter.core.trackEvent
import dev.veryniche.stitchcounter.data.models.ScreenOnState
import dev.veryniche.stitchcounter.wear.MainViewModel
import dev.veryniche.stitchcounter.wear.Screens
import dev.veryniche.stitchcounter.wear.presentation.whatsnew.WhatsNewScreen
import kotlinx.coroutines.launch

@Composable
fun NavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    listState: ScalingLazyListState,
    screenOnState: ScreenOnState,
    onScreenOnStateUpdate: (ScreenOnState) -> Unit,
    onTileStateUpdate: () -> Unit,
    ambientAwareState: AmbientStateUpdate,
    startDestination: String,
) {
    val coroutineScope = rememberCoroutineScope()
    SwipeDismissableNavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("about") {
            viewModel.updateCurrentScreen(Screens.About)
            AboutScreen(listState = listState)
        }
        composable("whats_new") {
            viewModel.updateCurrentScreen(Screens.WhatsNew)

            val whatsNewToShow by viewModel.whatsNewToShow.collectAsStateWithLifecycle(
                listOf(),
                lifecycleOwner = LocalLifecycleOwner.current
            )
            WhatsNewScreen(
                listState = listState,
                data = whatsNewToShow,
                onClose = {
                    viewModel.updateWhatsNewLastSeen(whatsNewToShow.maxOf { it.id })
                    navController.navigate("project_list")
                }
            )
        }
        composable("select_project_for_tile") {
            viewModel.updateCurrentScreen(Screens.SelectProjectForTile)
            SelectProjectForTileScreen(
                viewModel = viewModel,
                listState = listState,
                onProjectClick = { projectId ->
                    navController.navigate("select_counter_for_tile/$projectId")
                }
            )
        }
        composable("select_counter_for_tile/{id}") {
            it.arguments?.getString("id")?.toIntOrNull()?.let { projectId ->
                viewModel.updateCurrentScreen(Screens.SelectCounterForTile)
                SelectCounterForTileScreen(
                    projectId = projectId,
                    viewModel = viewModel,
                    listState = listState,
                    onCounterClick = { counterId ->
                        viewModel.updateTileState(projectId, counterId)
                    },
                    onConfirmationDialogClose = {
                        onTileStateUpdate.invoke()
                    }
                )
            }
        }
        composable("project_list") {
            viewModel.updateCurrentScreen(Screens.ProjectList)
            ProjectListScreen(
                viewModel = viewModel,
                listState = listState,
                onProjectClick = { projectId ->
                    navController.navigate("project/$projectId")
                },
                onAddProjectClick = {
                    trackEvent(Analytics.Action.AddProject, isMobile = false)
                    navController.navigate("edit_project")
                },
                onAboutClick = {
                    coroutineScope.launch {
                        listState.scrollToItem(index = 0)
                    }
                    navController.navigate("about")
                }
            )
        }
        composable("project/{id}") {
            it.arguments?.getString("id")?.toIntOrNull()?.let { projectId ->
                viewModel.updateCurrentScreen(Screens.Project)
                ProjectScreen(
                    viewModel = viewModel,
                    id = projectId,
                    listState = listState,
                    ambientAwareState = ambientAwareState,
                    onCounterClick = { counter ->
                        navController.navigate("counter/$projectId/${counter.id}")
                    },
                    onCounterAdd = { newCounterId ->
                        navController.navigate("edit_counter/$projectId/$newCounterId")
                    },
                    onProjectEdit = { id, name ->
                        navController.navigate("edit_project/$id/$name")
                    },
                    keepScreenOn = screenOnState.projectScreenOn,
                    onKeepScreenOnUpdate = { update ->
                        onScreenOnStateUpdate.invoke(screenOnState.copy(projectScreenOn = update))
                    },
                )
            }
        }
        composable("edit_project") {
            viewModel.updateCurrentScreen(Screens.EditProject)
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
                viewModel.updateCurrentScreen(Screens.EditProject)
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
                viewModel.updateCurrentScreen(Screens.Counter)
                CounterScreen(
                    viewModel = viewModel,
                    projectId = projectId,
                    counterId = counterId,
                    ambientAwareState = ambientAwareState,
                    keepScreenOn = screenOnState.counterScreenOn,
                    onKeepScreenOnUpdate = { update ->
                        onScreenOnStateUpdate.invoke(screenOnState.copy(counterScreenOn = update))
                    },
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
                viewModel.updateCurrentScreen(Screens.EditCounter)
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
                viewModel.updateCurrentScreen(Screens.EditCounter)
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
                viewModel.saveProjectName(projectId, projectName)
                navController.navigateUp()
            }
        },
        onDelete = {
            composableScope.launch {
                projectId?.let {
                    trackEvent(Analytics.Action.DeleteProject, isMobile = false)
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
                trackEvent(Analytics.Action.DeleteCounter, isMobile = false)
                viewModel.deleteCounter(projectId, counterId)
                navController.navigateUp()
            }
        },
        onClose = {
            navController.navigateUp()
        }
    )
}
