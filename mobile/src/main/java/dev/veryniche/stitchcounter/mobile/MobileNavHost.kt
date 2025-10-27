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
import androidx.compose.ui.platform.LocalContext
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
import dev.veryniche.stitchcounter.mobile.components.WearAppInfoDialog
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseAction
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseStatus
import dev.veryniche.stitchcounter.mobile.screens.AboutScreen
import dev.veryniche.stitchcounter.mobile.screens.ProjectListScreen
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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val defaultProjectName = stringResource(R.string.project_name_default)
    val availableSubscriptions by viewModel.availableSubscriptions.collectAsStateWithLifecycle(listOf())
    var showPurchaseDialogMessage by rememberSaveable { mutableStateOf<Int?>(null) }
    val showWearAppInfo by viewModel.isUninstalledWatchAvailable.collectAsStateWithLifecycle(false)
    val isPhoneAppInfoDoNotShow by viewModel.isConnectedAppInfoDoNotShow.collectAsStateWithLifecycle(true)

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
                showWearAppInfo = showWearAppInfo,
                onInstallWearAppClick = {
                    viewModel.installAppOnWatch(context)
                }
            )
        }
        composable<SettingsDestination> {
            onKeepScreenOnChanged.invoke(false)
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
            }
        }
        composable<CounterDestination> { backstackNavigation ->
            onKeepScreenOnChanged.invoke(keepScreenOnState.counterScreenOn)
            val arguments = backstackNavigation.toRoute<CounterDestination>()
            val coroutineScope = rememberCoroutineScope()
            val projectState by viewModel.getProject(arguments.projectId).collectAsState(null)
            projectState?.counters?.firstOrNull { it.id == arguments.counterId }?.let { counter ->
                
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

    if (!isPhoneAppInfoDoNotShow && showWearAppInfo) {
        WearAppInfoDialog(
            onDismiss = { doNotShowAgain ->
                showPurchaseDialogMessage = null
                viewModel.updateIsConnectedAppInfoDoNotShow(!doNotShowAgain)
            },
            onInstallWearAppClick = { doNotShowAgain ->
                viewModel.installAppOnWatch(context)
                viewModel.updateIsConnectedAppInfoDoNotShow(!doNotShowAgain)
            },
        )
    }
}
