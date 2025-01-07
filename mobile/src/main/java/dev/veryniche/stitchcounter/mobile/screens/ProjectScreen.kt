package dev.veryniche.stitchcounter.mobile.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import dev.veryniche.stitchcounter.core.Analytics.Action
import dev.veryniche.stitchcounter.core.Analytics.Screen
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.core.TrackedScreen
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.core.trackEvent
import dev.veryniche.stitchcounter.core.trackScreenView
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.mobile.BuildConfig
import dev.veryniche.stitchcounter.mobile.ads.BannerAd
import dev.veryniche.stitchcounter.mobile.ads.BannerAdLocation
import dev.veryniche.stitchcounter.mobile.components.AboutActionIcon
import dev.veryniche.stitchcounter.mobile.components.CollapsedTopAppBar
import dev.veryniche.stitchcounter.mobile.components.CounterListItemComponent
import dev.veryniche.stitchcounter.mobile.components.DeleteActionIcon
import dev.veryniche.stitchcounter.mobile.components.DeleteProjectConfirmation
import dev.veryniche.stitchcounter.mobile.components.EditActionIcon
import dev.veryniche.stitchcounter.mobile.components.ExpandingTopAppBar
import dev.veryniche.stitchcounter.mobile.components.NavigationIcon
import dev.veryniche.stitchcounter.mobile.components.SaveProjectConfirmation
import dev.veryniche.stitchcounter.mobile.components.SettingsActionIcon
import dev.veryniche.stitchcounter.mobile.conditional
import dev.veryniche.stitchcounter.mobile.previews.PreviewScreen
import dev.veryniche.stitchcounter.mobile.purchase.PurchaseStatus
import dev.veryniche.stitchcounter.mobile.snapshotStateListSaver
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProjectScreen(
    project: Project,
    purchaseStatus: PurchaseStatus,
    projectEditMode: Boolean = false,
    onAboutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSave: (project: Project) -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit,
    onAddCounter: (() -> Unit) -> Unit,
    onOpenCounter: (Counter) -> Unit,
    onCounterUpdate: (Counter) -> Unit,
    onCounterDelete: (Counter) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
) {
    val projectNameDefault = stringResource(R.string.project_name_default)
    var projectName by rememberSaveable {
        mutableStateOf(
            project.name.ifBlank {
                projectNameDefault
            }
        )
    }
    var showProjectEditMode by rememberSaveable { mutableStateOf(projectEditMode) }
    var showDeleteProjectConfirmation by rememberSaveable { mutableStateOf(false) }
    var showSaveProjectConfirmation by rememberSaveable { mutableStateOf(false) }
    val countersInEditMode = rememberSaveable(saver = snapshotStateListSaver()) { mutableStateListOf<Int>() }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    TrackedScreen {
        trackScreenView(name = Screen.Project, isMobile = true)
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            if (windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT || (windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.MEDIUM && windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED)) {
                CollapsedTopAppBar(
                    titleText = if (BuildConfig.SHOW_IDS) {
                        "$projectName (${project.id})"
                    } else {
                        projectName
                    },
                    actions = {
                        if (!showProjectEditMode) {
                            EditActionIcon { showProjectEditMode = true }
                        }
                        DeleteActionIcon { showDeleteProjectConfirmation = true }
                        SettingsActionIcon { onSettingsClick.invoke() }
                        AboutActionIcon { onAboutClick.invoke() }
                    },
                    navigationIcon = {
                        NavigationIcon {
                            if (showProjectEditMode) {
                                showSaveProjectConfirmation = true
                            } else {
                                onBack.invoke()
                            }
                        }
                    }
                )
            } else {
                ExpandingTopAppBar(
                    titleText = if (BuildConfig.SHOW_IDS) {
                        "$projectName (${project.id})"
                    } else {
                        projectName
                    },
                    actions = {
                        if (!showProjectEditMode) {
                            EditActionIcon { showProjectEditMode = true }
                        }
                        DeleteActionIcon { showDeleteProjectConfirmation = true }
                        SettingsActionIcon { onSettingsClick.invoke() }
                        AboutActionIcon { onAboutClick.invoke() }
                    },
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        NavigationIcon {
                            if (showProjectEditMode) {
                                showSaveProjectConfirmation = true
                            } else {
                                onBack.invoke()
                            }
                        }
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            val fabModifier = Modifier
            if (showProjectEditMode) {
                ExtendedFloatingActionButton(
                    onClick = {
                        trackEvent(Action.EditProjectSave, isMobile = true)
                        onSave.invoke(project.copy(name = projectName.trim()))
                        showProjectEditMode = false
                    },
                    icon = { Icon(Icons.Filled.Check, stringResource(id = R.string.add_project)) },
                    text = { Text(text = stringResource(id = R.string.save_project)) },
                    modifier = fabModifier
                )
            } else {
                ExtendedFloatingActionButton(
                    onClick = {
                        onAddCounter.invoke {
                            trackEvent(Action.AddCounter, isMobile = true)
                            val nextId = project.counters.maxOfOrNull { it.id }?.plus(1) ?: 0
                            val defaultCounterName =
                                context.getString(R.string.counter_name_default, nextId)
                            onCounterUpdate.invoke(Counter(id = nextId, name = defaultCounterName))
                            countersInEditMode.add(nextId)
                        }
                    },
                    icon = { Icon(Icons.Filled.Add, stringResource(id = R.string.add_counter)) },
                    text = { Text(text = stringResource(id = R.string.add_counter)) },
                    modifier = fabModifier
                )
            }
        },
        bottomBar = {
            if (!LocalInspectionMode.current && !purchaseStatus.isBundleSubscribed) {
                BannerAd(
                    location = BannerAdLocation.ProjectScreen,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .imePadding()
                )
            }
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        Column(
            Modifier.padding(contentPadding)
        ) {
            if (showProjectEditMode) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimen.spacing),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimen.spacingDouble)
                    ) {
                        OutlinedTextField(
                            value = projectName,
                            onValueChange = { projectName = it },
                            singleLine = true,
                            isError = projectName.isBlank(),
                            label = {
                                Text(
                                    stringResource(dev.veryniche.stitchcounter.mobile.R.string.label_project_name)
                                )
                            },
                            placeholder = {
                                Text(
                                    stringResource(R.string.project_name_default)
                                )
                            },
                            supportingText = {
                                if (projectName.isBlank()) {
                                    Text(
                                        text = stringResource(
                                            dev.veryniche.stitchcounter.mobile.R.string.validation_message_project_name
                                        ),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = { keyboardController?.hide() }
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(id = R.string.edit_project),
                            modifier = Modifier.size(Dimen.mobileEditModeIconSize)
                        )
                    }
                }
            }
            if (project.counters.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = Dimen.spacingQuad)
                ) {
                    Text(
                        text = stringResource(id = dev.veryniche.stitchcounter.mobile.R.string.counter_list_empty),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                LazyVerticalGrid(
                    verticalArrangement = Arrangement.spacedBy(Dimen.spacingQuad),
                    horizontalArrangement = Arrangement.spacedBy(Dimen.spacingQuad),
                    columns = Fixed(
                        when (windowSizeClass.windowWidthSizeClass) {
                            WindowWidthSizeClass.EXPANDED,
                            WindowWidthSizeClass.MEDIUM -> 2
                            else -> 1
                        }
                    ),
                    contentPadding = PaddingValues(Dimen.spacingQuad),
                    modifier = modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    itemsIndexed(project.counters) { index, counter ->
                        CounterListItemComponent(
                            counter = counter,
                            onCounterUpdate = { updatedCounter ->
                                countersInEditMode.remove(counter.id)
                                onCounterUpdate.invoke(updatedCounter)
                            },
                            onCounterDelete = {
                                countersInEditMode.remove(counter.id)
                                onCounterDelete.invoke(counter)
                            },
                            inEditMode = countersInEditMode.contains(counter.id),
                            modifier = Modifier
                                .fillMaxWidth()
                                .conditional(
                                    !countersInEditMode.contains(counter.id),
                                    {
                                        combinedClickable(
                                            onClick = {
                                                onOpenCounter.invoke(counter)
                                            },
                                            onLongClick = {
                                                countersInEditMode.add(counter.id)
                                            }
                                        )
                                    }
                                ),
                        )
                    }
                    item(span = {
                        GridItemSpan(
                            when (windowSizeClass.windowWidthSizeClass) {
                                WindowWidthSizeClass.EXPANDED,
                                WindowWidthSizeClass.MEDIUM -> 2
                                else -> 1
                            }
                        )
                    }) {
                        Column(verticalArrangement = Arrangement.spacedBy(Dimen.spacingDouble)) {
                            Text(
                                stringResource(dev.veryniche.stitchcounter.mobile.R.string.project_instruction_text),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                stringResource(
                                    dev.veryniche.stitchcounter.mobile.R.string.project_last_updated,
                                    project.lastModifiedString
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .alpha(0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
    if (showSaveProjectConfirmation) {
        SaveProjectConfirmation(
            projectName = projectName,
            onAccept = {
                trackEvent(Action.EditProjectSave, isMobile = true)
                onSave.invoke(project.copy(name = projectName))
                showProjectEditMode = false
                showSaveProjectConfirmation = false
                onBack.invoke()
            },
            onDismiss = {
                onBack.invoke()
            }
        )
    }
    if (showDeleteProjectConfirmation) {
        DeleteProjectConfirmation(
            projectName = projectName,
            onAccept = {
                onDelete.invoke()
            },
            onDismiss = {
                onBack.invoke()
            }
        )
    }
}

@PreviewScreen
@Composable
fun ProjectScreenPreview() {
    StitchCounterTheme {
        ProjectScreen(
            project = Project(
                name = "Project 1",
                counters = listOf(),
            ),
            onAboutClick = {},
            onSettingsClick = {},
            onSave = {},
            onDelete = {},
            onBack = {},
            onOpenCounter = {},
            onCounterDelete = {},
            onCounterUpdate = {},
            onAddCounter = {},
            purchaseStatus = PurchaseStatus(true),
        )
    }
}

@PreviewScreen
@Composable
fun ProjectScreenWithCountersPreview() {
    StitchCounterTheme {
        ProjectScreen(
            project = Project(
                name = "Project 2",
                counters = listOf(
                    Counter(id = 0, name = "Counter 1"),
                    Counter(id = 1, name = "Counter 2")
                ),
            ),
            onAboutClick = {},
            onSettingsClick = {},
            onSave = {},
            onDelete = {},
            onBack = {},
            onOpenCounter = {},
            onCounterDelete = {},
            onCounterUpdate = {},
            onAddCounter = {},
            purchaseStatus = PurchaseStatus(true),
        )
    }
}

@PreviewScreen
@Composable
fun ProjectScreenEditModePreview() {
    StitchCounterTheme {
        ProjectScreen(
            project = Project(
                name = "Project 2",
                counters = listOf(
                    Counter(id = 0, name = "Counter 1"),
                    Counter(id = 1, name = "Counter 2")
                ),
            ),
            projectEditMode = true,
            onAboutClick = {},
            onSettingsClick = {},
            onSave = {},
            onDelete = {},
            onBack = {},
            onOpenCounter = {},
            onCounterDelete = {},
            onCounterUpdate = {},
            onAddCounter = {},
            purchaseStatus = PurchaseStatus(true),
        )
    }
}
