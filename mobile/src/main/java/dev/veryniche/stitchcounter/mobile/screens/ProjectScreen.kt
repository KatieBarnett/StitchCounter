package dev.veryniche.stitchcounter.mobile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import dev.veryniche.stitchcounter.core.AnalyticsConstants
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.mobile.TrackedScreen
import dev.veryniche.stitchcounter.mobile.ads.BannerAd
import dev.veryniche.stitchcounter.mobile.ads.BannerAdLocation
import dev.veryniche.stitchcounter.mobile.components.AboutActionIcon
import dev.veryniche.stitchcounter.mobile.components.NavigationIcon
import dev.veryniche.stitchcounter.mobile.components.topAppBarColors
import dev.veryniche.stitchcounter.mobile.previews.PreviewComponent
import dev.veryniche.stitchcounter.mobile.previews.PreviewScreen
import dev.veryniche.stitchcounter.mobile.trackEvent
import dev.veryniche.stitchcounter.mobile.trackScreenView
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

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
    val projectNameHint = stringResource(id = R.string.project_name_default)
    var projectName by remember { mutableStateOf(project.name) }
    var projectState by remember { mutableStateOf(project) }
    var showProjectEditMode by remember { mutableStateOf(projectEditMode) }
    var showDeleteProjectDialog by remember { mutableStateOf(false) }
    var showDeleteProjectConfirmation by remember { mutableStateOf(false) }
    var showSaveProjectConfirmation by remember { mutableStateOf(false) }

    TrackedScreen {
        trackScreenView(name = AnalyticsConstants.Screen.Project)
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = projectName,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                colors = topAppBarColors,
                actions = {
                    AboutActionIcon { onAboutClick.invoke() }
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    if (showProjectEditMode) {
                        showSaveProjectConfirmation = true
                    } else {
                        NavigationIcon { onBack.invoke() }
                    }
                }
            )
        },
        floatingActionButton = {
            if (showProjectEditMode) {
                ExtendedFloatingActionButton(
                    onClick = {
                        trackEvent(AnalyticsConstants.Action.EditProjectSave)
                        onSave.invoke(projectState)
                        showProjectEditMode = false
                    },
                    icon = { Icon(Icons.Filled.Check, stringResource(id = R.string.add_project)) },
                    text = { Text(text = stringResource(id = R.string.save_project)) },
                )
            } else {
                ExtendedFloatingActionButton(
                    onClick = {
                        trackEvent(AnalyticsConstants.Action.EditProject)
                        showProjectEditMode = true
                    },
                    icon = { Icon(Icons.Filled.Edit, stringResource(id = R.string.edit_project)) },
                    text = { Text(text = stringResource(id = R.string.edit_project)) },
                )
                ExtendedFloatingActionButton(
                    onClick = {
                        trackEvent(AnalyticsConstants.Action.AddCounter)
                    },
                    icon = { Icon(Icons.Filled.Add, stringResource(id = R.string.add_counter)) },
                    text = { Text(text = stringResource(id = R.string.add_counter)) },
                )
            }
        },
        bottomBar = {
            BannerAd(location = BannerAdLocation.ProjectScreen)
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        Column(Modifier.padding(contentPadding)) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Dimen.spacingQuad),
                modifier = modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = Dimen.spacingQuad)
            ) {
                items(projectState.counters) { counter ->
//                    ProjectItem(project, onProjectClick, Modifier.fillMaxWidth())
                }
            }
        }
//        Column(
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = modifier.fillMaxSize().padding(contentPadding)
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth(0.85f)
//            ) {
//                OutlinedTextField(
//                    value = projectName.orEmpty(),
//                    onValueChange = { projectName = it },
//                    isError = name.isNullOrBlank(),
//                    label = { Text(stringResource(R.string.label_name)) },
//                    supportingText = {
//                        if (name.isNullOrBlank()) {
//                            Text(
//                                text = stringResource(R.string.validation_message_name),
//                                color = MaterialTheme.colorScheme.error
//                            )
//                        }
//                    }
//                )
//
//                Text(text = projectName, Modifier.weight(1f))
//                CompactButton(
//                    onClick = { launcher.launch(intent) },
//                    colors = ButtonDefaults.secondaryButtonColors()
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.Edit,
//                        contentDescription = stringResource(id = R.string.edit_project_name)
//                    )
//                }
//            }
//            Row(
//                horizontalArrangement = Arrangement.Center,
//                modifier = Modifier.fillMaxWidth(0.85f)
//            ) {
//                CompactButton(
//                    onClick = { onClose.invoke() },
//                    colors = ButtonDefaults.secondaryButtonColors()
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.Close,
//                        contentDescription = stringResource(id = R.string.cancel_edit_project)
//                    )
//                }
//                if (initialName != null) {
//                    CompactButton(
//                        onClick = { showDeleteProjectDialog = true },
//                        colors = ButtonDefaults.secondaryButtonColors()
//                    ) {
//                        Icon(
//                            imageVector = Icons.Filled.Delete,
//                            contentDescription = stringResource(id = R.string.delete_project)
//                        )
//                    }
//                }
//            }
    }
    if (showSaveProjectConfirmation) {
        SaveProjectConfirmation(
            projectName = projectName,
            onAccept = {
                trackEvent(AnalyticsConstants.Action.EditProjectSave)
                onSave.invoke(projectState)
            },
            onDismiss =  {
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
            onDismiss =  {
                onBack.invoke()
            }
        )
    }
}

@Composable
fun SaveProjectConfirmation(projectName: String, onAccept: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss.invoke() },
        title = { Text(projectName) },
        text = { Text(stringResource(R.string.confirm_project_save)) },
        confirmButton = {
            TextButton(onClick = {
                trackEvent(AnalyticsConstants.Action.EditProjectConfim)
                onAccept.invoke()
            }) {
                Text(stringResource(R.string.confirm_changes_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss.invoke()
            }) {
                Text(stringResource(R.string.confirm_changes_dismiss))
            }
        }
    )
}

@Composable
fun DeleteProjectConfirmation(projectName: String, onAccept: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss.invoke() },
        title = { Text(projectName) },
        text = { Text(stringResource(R.string.confirm_project_delete)) },
        confirmButton = {
            TextButton(onClick = {
                trackEvent(AnalyticsConstants.Action.DeleteProjectConfirm)
                onAccept.invoke()
            }) {
                Text(stringResource(R.string.confirm_changes_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss.invoke()
            }) {
                Text(stringResource(R.string.confirm_changes_dismiss))
            }
        }
    )
}

@PreviewComponent
@Composable
fun SaveProjectConfirmationPreview() {
    StitchCounterTheme {
        SaveProjectConfirmation("Project Name that is really long", {}, {})
    }
}

@PreviewComponent
@Composable
fun DeleteProjectConfirmationPreview() {
    StitchCounterTheme {
        DeleteProjectConfirmation("Project Name that is really long", {}, {})
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
