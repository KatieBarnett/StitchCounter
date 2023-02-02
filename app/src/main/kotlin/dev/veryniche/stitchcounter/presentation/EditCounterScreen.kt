package dev.veryniche.stitchcounter.presentation

import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import dev.veryniche.stitchcounter.R
import dev.veryniche.stitchcounter.R.string
import dev.veryniche.stitchcounter.presentation.theme.Dimen
import dev.veryniche.stitchcounter.presentation.theme.StitchCounterTheme
import dev.veryniche.stitchcounter.util.Analytics
import dev.veryniche.stitchcounter.util.TrackedScreen
import dev.veryniche.stitchcounter.util.trackScreenView

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditCounterScreen(
    counterId: Int,
    initialName: String?,
    initialMax: Int,// = 0,
    onSave: (counterName: String, counterMax: Int) -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val counterDefaultName = initialName ?: stringResource(R.string.counter_name_default, counterId)
    var counterName by remember { mutableStateOf(counterDefaultName) }
    var counterMax by remember { mutableStateOf(initialMax) }
    var showEditCounterMaxDialog by remember { mutableStateOf(false) }
    var showDeleteCounterDialog by remember { mutableStateOf(false) }
    var showDeleteCounterConfirmation by remember { mutableStateOf(false) }

    TrackedScreen {
        trackScreenView(name = Analytics.Screen.EditCounter)
    }

    val inputTextKey = "input_text"
    val intent: Intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
    val remoteInputs: List<RemoteInput> = listOf(
        RemoteInput.Builder(inputTextKey)
            .setLabel(stringResource(R.string.edit_counter_name))
            .wearableExtender {
                setEmojisAllowed(true)
                setInputActionType(EditorInfo.IME_ACTION_DONE)
            }.build()
    )

    val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.let { data ->
                val results: Bundle = RemoteInput.getResultsFromIntent(data)
                val newInputText: CharSequence? = results.getCharSequence(inputTextKey)
                counterName = newInputText as String
            }
        }

    RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Spacer(Modifier.height(Dimen.spacingHuge))
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.75f)) {
            Text(text = counterName, Modifier.weight(1f))
            CompactButton(
                onClick = { launcher.launch(intent) },
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(id = R.string.edit_counter_name)
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.95f)) {
            Text(if (counterMax == 0) {
                stringResource(R.string.counter_max_label_zero)
            } else {
                stringResource(R.string.counter_max_label_many, counterMax)
            }, Modifier.weight(1f))
            CompactButton(
                onClick = { showEditCounterMaxDialog = true },
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(id = R.string.edit_counter_max)
                )
            }
        }
        Row(horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(0.75f)) {
            CompactButton(
                onClick = { onClose.invoke() },
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Icon(
                    imageVector = Filled.Close,
                    contentDescription = stringResource(id = string.cancel_edit_project)
                )
            }
            if (initialName != null) {
                CompactButton(
                    onClick = { showDeleteCounterDialog = true },
                    colors = ButtonDefaults.secondaryButtonColors()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(id = R.string.delete_counter)
                    )
                }
            }
            CompactButton(
                onClick = { 
                    onSave.invoke(counterName, counterMax) 
                },
                colors = ButtonDefaults.primaryButtonColors()
            ) {
                Icon(
                    imageVector = Filled.Check,
                    contentDescription = stringResource(id = string.save_project)
                )
            }
        }
        Spacer(Modifier.height(Dimen.spacingHuge))
    }
    EditCounterMaxDialog(
        showDialog = showEditCounterMaxDialog,
        initialValue = counterMax,
        onDismissRequest = { showEditCounterMaxDialog = false },
        onDone = { 
            counterMax = it
            showEditCounterMaxDialog = false
        }
    )
    if (showDeleteCounterDialog) {
        DeleteCounterAlert(
            counterName = counterName,
            onConfirm = {
                showDeleteCounterDialog = false
                showDeleteCounterConfirmation = true
            },
            onCancel = {
                showDeleteCounterDialog = false
            }
        )
    }
    if (showDeleteCounterConfirmation) {
        DeleteCounterConfirmation(
            counterName = counterName,
            onTimeout = {
                onDelete.invoke()
                onClose.invoke()
            }
        )
    }
}

@Composable
fun DeleteCounterAlert(counterName: String, onConfirm: () -> Unit, onCancel: () -> Unit) {
    Alert(
        title = {
            Text(stringResource(string.delete_project_message, counterName))
        },
        negativeButton = {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Icon(
                    imageVector = Filled.Close,
                    contentDescription = stringResource(string.delete_counter_negative)
                )
            }
        },
        positiveButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.primaryButtonColors()
            ) {
                Icon(
                    imageVector = Filled.Check,
                    contentDescription = stringResource(string.delete_counter_positive)
                )
            }
        }
    )
}

@Composable
fun DeleteCounterConfirmation(counterName: String, onTimeout: () -> Unit) {
    Confirmation(
        onTimeout = onTimeout,
        icon = {
            Icon(
                imageVector = Filled.Check,
                contentDescription = stringResource(string.delete_counter_positive),
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(Dimen.confirmationIconSize)
            )
        },
        content = {
            Text(stringResource(string.delete_counter_success, counterName))
        }
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun EditCounterScreenPreview() {
    StitchCounterTheme {
        EditCounterScreen(1, "initial name", 45, {_, _ -> }, {}, {})
    }
}
