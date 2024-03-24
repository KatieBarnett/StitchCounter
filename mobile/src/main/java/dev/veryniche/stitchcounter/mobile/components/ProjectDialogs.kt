package dev.veryniche.stitchcounter.mobile.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.veryniche.stitchcounter.core.AnalyticsConstants
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.mobile.previews.PreviewComponent
import dev.veryniche.stitchcounter.mobile.trackEvent
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@Composable
fun SaveProjectEnterName(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss.invoke() },
        title = { Text(stringResource(dev.veryniche.stitchcounter.mobile.R.string.validation_error)) },
        text = { Text(stringResource(dev.veryniche.stitchcounter.mobile.R.string.validation_message_project_name)) },
        confirmButton = {
            TextButton(onClick = {
                onDismiss.invoke()
            }) {
                Text(stringResource(R.string.confirm_changes_confirm))
            }
        },
    )
}

@Composable
fun SaveProjectConfirmation(projectName: String, onAccept: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss.invoke() },
        title = { Text(projectName) },
        text = { Text(stringResource(dev.veryniche.stitchcounter.mobile.R.string.confirm_project_save)) },
        confirmButton = {
            TextButton(onClick = {
                trackEvent(AnalyticsConstants.Action.EditProjectConfim)
                onAccept.invoke()
            }) {
                Text(stringResource(dev.veryniche.stitchcounter.mobile.R.string.confirm_project_save_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss.invoke()
            }) {
                Text(stringResource(dev.veryniche.stitchcounter.mobile.R.string.confirm_project_save_dismiss))
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
fun SaveProjectEnterNamePreview() {
    StitchCounterTheme {
        SaveProjectEnterName({})
    }
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
