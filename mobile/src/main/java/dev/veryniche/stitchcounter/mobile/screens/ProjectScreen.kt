package dev.veryniche.stitchcounter.mobile.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import dev.veryniche.stitchcounter.core.AnalyticsConstants
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.mobile.TrackedScreen
import dev.veryniche.stitchcounter.mobile.ads.BannerAd
import dev.veryniche.stitchcounter.mobile.ads.BannerAdLocation
import dev.veryniche.stitchcounter.mobile.components.AboutActionIcon
import dev.veryniche.stitchcounter.mobile.components.CounterListItemComponent
import dev.veryniche.stitchcounter.mobile.components.DeleteActionIcon
import dev.veryniche.stitchcounter.mobile.components.DeleteProjectConfirmation
import dev.veryniche.stitchcounter.mobile.components.EditActionIcon
import dev.veryniche.stitchcounter.mobile.components.NavigationIcon
import dev.veryniche.stitchcounter.mobile.components.SaveProjectConfirmation
import dev.veryniche.stitchcounter.mobile.components.topAppBarColors
import dev.veryniche.stitchcounter.mobile.previews.PreviewScreen
import dev.veryniche.stitchcounter.mobile.trackEvent
import dev.veryniche.stitchcounter.mobile.trackScreenView
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectScreen(
    project: Project,
    projectEditMode: Boolean = false,
    onAboutClick: () -> Unit,
    onSave: (project: Project) -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var projectName by remember { mutableStateOf(project.name) }
    var projectState by remember { mutableStateOf(project) }
    var showProjectEditMode by remember { mutableStateOf(projectEditMode) }
    var showDeleteProjectConfirmation by remember { mutableStateOf(false) }
    var showSaveProjectConfirmation by remember { mutableStateOf(false) }
    val isKeyboardOpen by keyboardAsState()
    TrackedScreen {
        trackScreenView(name = AnalyticsConstants.Screen.Project)
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = projectName.ifBlank {
                            stringResource(R.string.project_name_default)
                        },
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                colors = topAppBarColors,
                actions = {
                    if (!showProjectEditMode) {
                        EditActionIcon { showProjectEditMode = true }
                    }
                    DeleteActionIcon { showDeleteProjectConfirmation = true }
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
        },
        floatingActionButton = {
            val fabModifier = Modifier
            if (showProjectEditMode) {
                ExtendedFloatingActionButton(
                    onClick = {
                        trackEvent(AnalyticsConstants.Action.EditProjectSave)
                        onSave.invoke(projectState.copy(name = projectName))
                        showProjectEditMode = false
                    },
                    icon = { Icon(Icons.Filled.Check, stringResource(id = R.string.add_project)) },
                    text = { Text(text = stringResource(id = R.string.save_project)) },
                    modifier = fabModifier
                )
            } else {
                ExtendedFloatingActionButton(
                    onClick = {
                        trackEvent(AnalyticsConstants.Action.EditProject)
                        showProjectEditMode = true
                    },
                    icon = { Icon(Icons.Filled.Edit, stringResource(id = R.string.edit_project)) },
                    text = { Text(text = stringResource(id = R.string.edit_project)) },
                    modifier = fabModifier
                )
                ExtendedFloatingActionButton(
                    onClick = {
                        trackEvent(AnalyticsConstants.Action.AddCounter)
                    },
                    icon = { Icon(Icons.Filled.Add, stringResource(id = R.string.add_counter)) },
                    text = { Text(text = stringResource(id = R.string.add_counter)) },
                    modifier = fabModifier
                )
            }
        },
        bottomBar = {
            BannerAd(
                location = BannerAdLocation.ProjectScreen,
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            )
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
                            onValueChange = { projectName = it.trim() },
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
            if (projectState.counters.isEmpty()) {
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
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Dimen.spacingQuad),
                    modifier = modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = Dimen.spacingQuad)
                ) {
                    item {
                        // Spacer
                    }
                    itemsIndexed(projectState.counters) { index, counter ->
                        CounterListItemComponent(
                            counter = counter,
                            onCounterUpdate = {},
                            onCounterDelete = {},
                            defaultCounterName = stringResource(id = R.string.counter_name_default, index),
                            inEditMode = projectEditMode,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        // Spacer
                    }
                }
            }
        }
    }
    if (showSaveProjectConfirmation) {
        SaveProjectConfirmation(
            projectName = projectName,
            onAccept = {
                trackEvent(AnalyticsConstants.Action.EditProjectSave)
                onSave.invoke(projectState)
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
            onSave = {},
            onDelete = {},
            onBack = {}
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
            onSave = {},
            onDelete = {},
            onBack = {}
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
            onSave = {},
            onDelete = {},
            onBack = {}
        )
    }
}
