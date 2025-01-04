package dev.veryniche.stitchcounter.mobile

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.window.core.layout.WindowSizeClass
import dev.veryniche.stitchcounter.core.Analytics.Action
import dev.veryniche.stitchcounter.core.Analytics.Action.ProPurchaseRequiredCounter
import dev.veryniche.stitchcounter.core.Analytics.Action.ProPurchaseRequiredProject
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.core.trackEvent
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.data.models.ScreenOnState
import dev.veryniche.stitchcounter.mobile.components.PurchaseDialog
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseAction
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseStatus
import dev.veryniche.stitchcounter.mobile.screens.AboutScreen
import dev.veryniche.stitchcounter.mobile.screens.CounterScreen
import dev.veryniche.stitchcounter.mobile.screens.ProjectListScreen
import dev.veryniche.stitchcounter.mobile.screens.ProjectScreen
import dev.veryniche.stitchcounter.mobile.screens.SettingsScreen
import dev.veryniche.stitchcounter.storage.ThemeMode
import kotlinx.coroutines.launch

@Composable
fun MobileNavHost(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: MainViewModel,
    purchaseStatus: PurchaseStatus,
    onPurchaseClick: (PurchaseAction) -> Unit,
    windowSizeClass: WindowSizeClass,
    themeMode: ThemeMode,
    keepScreenOnState: ScreenOnState,
    onKeepScreenOnChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val defaultProjectName = stringResource(R.string.project_name_default)
    val availableSubscriptions by viewModel.availableSubscriptions.collectAsStateWithLifecycle(listOf())
    var showPurchaseDialogMessage by rememberSaveable { mutableStateOf<Int?>(null) }
    NavHost(
        navController = navController,
        startDestination = ProjectListDestination,
        modifier = modifier
    ) {
        composable<AboutDestination> {
            onKeepScreenOnChanged.invoke(false)
            AboutScreen(
                onNavigateBack = { navController.navigateUp() },
                onSettingsClick = { navController.navigate(SettingsDestination) },
                purchaseStatus = purchaseStatus,
                onPurchaseClick = onPurchaseClick,
                snackbarHostState = snackbarHostState,
                availableSubscriptions = availableSubscriptions,
            )
        }
        composable<SettingsDestination> {
            onKeepScreenOnChanged.invoke(false)
            SettingsScreen(
                purchaseStatus = purchaseStatus,
                onNavigateBack = { navController.navigateUp() },
                onAboutClick = {
                    navController.navigate(AboutDestination)
                },
                onPurchaseClick = onPurchaseClick,
                onThemeModeSelected = {
                    viewModel.updateThemeMode(it)
                },
                onKeepScreenOnStateSelected = {
                    viewModel.updateScreenOnState(it)
                },
                snackbarHostState = snackbarHostState,
                themeMode = themeMode,
                keepScreenOnState = keepScreenOnState,
                availableSubscriptions = availableSubscriptions,
            )
        }
        composable<ProjectListDestination> {
            onKeepScreenOnChanged.invoke(false)
            val coroutineScope = rememberCoroutineScope()
            val projects: List<Project> by viewModel.projects.collectAsState(initial = emptyList())
            ProjectListScreen(
                projectList = projects,
                onProjectClick = { projectId ->
                    navController.navigate(ProjectDestination(projectId, false))
                },
                onAddProjectClick = {
                    if (projects.isEmpty() || purchaseStatus.isBundleSubscribed) {
                        coroutineScope.launch {
                            val newProjectId =
                                viewModel.saveProject(Project(name = defaultProjectName))
                            navController.navigate(ProjectDestination(newProjectId, true))
                        }
                    } else {
                        trackEvent(ProPurchaseRequiredProject, isMobile = true)
                        showPurchaseDialogMessage = dev.veryniche.stitchcounter.mobile.R.string.purchase_limit_projects
                    }
                },
                onAboutClick = {
                    navController.navigate(AboutDestination)
                },
                snackbarHostState = snackbarHostState,
                windowSizeClass = windowSizeClass,
                onSettingsClick = { navController.navigate(SettingsDestination) },
                purchaseStatus = purchaseStatus,
            )
        }
        composable<ProjectDestination> { backstackNavigation ->
            onKeepScreenOnChanged.invoke(keepScreenOnState.projectScreenOn)
            val arguments = backstackNavigation.toRoute<ProjectDestination>()
            val coroutineScope = rememberCoroutineScope()
            val projects: List<Project> by viewModel.projects.collectAsState(initial = emptyList())
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
                    onAddCounter = { nextAction ->
                        if ((projectState?.counters?.isEmpty() != false && projects.size == 1) ||
                            purchaseStatus.isBundleSubscribed
                        ) {
                            nextAction.invoke()
                        } else {
                            trackEvent(ProPurchaseRequiredCounter, isMobile = true)
                            showPurchaseDialogMessage = dev.veryniche.stitchcounter.mobile.R.string.purchase_limit_counters
                        }
                    },
                    snackbarHostState = snackbarHostState,
                    windowSizeClass = windowSizeClass,
                    onSettingsClick = { navController.navigate(SettingsDestination) },
                    purchaseStatus = purchaseStatus,
                )
            }
        }
        composable<CounterDestination> { backstackNavigation ->
            onKeepScreenOnChanged.invoke(keepScreenOnState.counterScreenOn)
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
                    onSettingsClick = { navController.navigate(SettingsDestination) },
                    purchaseStatus = purchaseStatus,
                )
            }
        }
    }

    showPurchaseDialogMessage?.let {
        PurchaseDialog(
            message = it,
            availableSubscriptions = availableSubscriptions,
            onDismiss = {
                showPurchaseDialogMessage = null
            },
            onPurchaseClick = { purchaseAction ->
                onPurchaseClick.invoke(purchaseAction)
                showPurchaseDialogMessage = null
            }
        )
    }
}
