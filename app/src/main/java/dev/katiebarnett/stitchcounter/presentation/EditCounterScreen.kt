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
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender
import dev.katiebarnett.stitchcounter.R
import dev.katiebarnett.stitchcounter.R.string
import dev.katiebarnett.stitchcounter.presentation.theme.StitchCounterTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditCounterScreen(
    counterId: Int,
    initialName: String?,
    onSave: (counterName: String, counterMax: Int) -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val counterDefaultName = initialName ?: stringResource(R.string.counter_name_default, counterId)
    var counterName by remember { mutableStateOf(counterDefaultName) }
    var counterMax by remember { mutableStateOf(0) }
    var showEditCounterMaxDialog by remember { mutableStateOf(false) }

    val inputTextKey = "input_text"
    val intent: Intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
    val remoteInputs: List<RemoteInput> = listOf(
        RemoteInput.Builder(inputTextKey)
            .setLabel(stringResource(R.string.counter_name_default, counterId))
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
            Text(pluralStringResource(R.plurals.counter_max_label, counterMax, counterMax), Modifier.weight(1f))
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
        Row(horizontalArrangement = Arrangement.SpaceBetween, 
            modifier = Modifier.fillMaxWidth(0.75f)) {
            CompactButton(
                onClick = { onCancel.invoke() },
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Icon(
                    imageVector = Filled.Close,
                    contentDescription = stringResource(id = string.cancel_edit_project)
                )
            }
            CompactButton(
                onClick = { onDelete.invoke() },
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(id = R.string.delete_counter)
                )
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
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun EditCounterScreenPreview() {
    StitchCounterTheme {
        EditCounterScreen(1, "initial name", {_, _ -> }, {}, {})
    }
}
