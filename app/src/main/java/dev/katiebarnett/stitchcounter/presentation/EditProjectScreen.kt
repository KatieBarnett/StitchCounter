package dev.katiebarnett.stitchcounter.presentation

import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Confirmation
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender
import dev.katiebarnett.stitchcounter.R
import dev.katiebarnett.stitchcounter.presentation.theme.Dimen
import dev.katiebarnett.stitchcounter.presentation.theme.StitchCounterTheme

@Composable
fun EditProjectScreen(
    initialName: String? = null,
    onSave: (projectName: String) -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val projectNameHint = stringResource(id = R.string.project_name_default)
    var projectName by remember { mutableStateOf(initialName ?: projectNameHint) }
    var showDeleteProjectDialog by remember { mutableStateOf(false) }
    var showDeleteProjectConfirmation by remember { mutableStateOf(false) }

    val inputTextKey = "input_text"
    val intent: Intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
    val remoteInputs: List<RemoteInput> = listOf(
        RemoteInput.Builder(inputTextKey)
            .setLabel(stringResource(R.string.edit_project_name))
            .wearableExtender {
                setEmojisAllowed(true)
                setInputActionType(EditorInfo.IME_ACTION_DONE)
            }.build()
    )
    RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)

    val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.let { data ->
                val results: Bundle = RemoteInput.getResultsFromIntent(data)
                val newInputText: CharSequence? = results.getCharSequence(inputTextKey)
                projectName = newInputText as String
            }
        }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.85f)){
            Text(text = projectName, Modifier.weight(1f))
            CompactButton(
                onClick = { launcher.launch(intent) },
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(id = R.string.edit_project_name)
                )
            }
        }
        Row(horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(0.85f)) {
            CompactButton(
                onClick = { onClose.invoke() },
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(id = R.string.cancel_edit_project)
                )
            }
            if (initialName != null) {
                CompactButton(
                    onClick = { showDeleteProjectDialog = true },
                    colors = ButtonDefaults.secondaryButtonColors()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(id = R.string.delete_project)
                    )
                }
            }
            CompactButton(
                onClick = { onSave.invoke(projectName) },
                colors = ButtonDefaults.primaryButtonColors()
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(id = R.string.save_project)
                )
            }
        }
    }
    if (showDeleteProjectDialog) {
        DeleteProjectAlert(
            projectName = projectName,
            onConfirm = {
                showDeleteProjectDialog = false
                showDeleteProjectConfirmation = true
            },
            onCancel = {
                showDeleteProjectDialog = false
            }
        )
    }
    if (showDeleteProjectConfirmation) {
        DeleteProjectConfirmation(
            projectName = projectName,
            onTimeout = {
                onDelete.invoke()
                onClose.invoke()
            }
        )
    }
}

@Composable
fun DeleteProjectAlert(projectName: String, onConfirm: () -> Unit, onCancel: () -> Unit) {
        Alert(
            title = {
                Text(stringResource(R.string.delete_project_message, projectName))
            },
            negativeButton = {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.secondaryButtonColors()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.delete_project_negative)
                    )
                }
            },
            positiveButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.primaryButtonColors()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.delete_project_positive)
                    )
                }
            }
        )
}

@Composable
fun DeleteProjectConfirmation(projectName: String, onTimeout: () -> Unit) {
    Confirmation(
        onTimeout = onTimeout,
        icon = {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = stringResource(R.string.delete_project_positive),
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(Dimen.confirmationIconSize)
            )
        },
        content = {
            Text(stringResource(R.string.delete_project_success, projectName))
        }
    )
}


@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DeleteProjectAlertPreview() {
    StitchCounterTheme {
        DeleteProjectAlert("Project Name", {}, {})
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DeleteProjectConfirmationPreview() {
    StitchCounterTheme {
        DeleteProjectConfirmation("Project Name", {})
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun EditProjectScreenPreview() {
    StitchCounterTheme {
        EditProjectScreen(null, {}, {}, {})
    }
}
