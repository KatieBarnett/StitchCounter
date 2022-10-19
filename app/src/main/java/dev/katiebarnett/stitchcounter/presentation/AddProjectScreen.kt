package dev.katiebarnett.stitchcounter.presentation

import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender
import dev.katiebarnett.stitchcounter.MainViewModel
import dev.katiebarnett.stitchcounter.R
import dev.katiebarnett.stitchcounter.presentation.theme.Dimen

@Composable
fun AddProjectScreen(
    viewModel: MainViewModel,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    AddProjectContent(
        nextProjectId = viewModel.nextProjectId,
        onSave = {
            onComplete.invoke()
        },
        onCancel = {
            onComplete.invoke()
        },
        modifier = modifier)
}

@Composable
fun AddProjectContent(
    nextProjectId: Int,
    onSave: (projectName: String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val projectDefaultName = stringResource(id = R.string.project_name_default, nextProjectId)
    var projectName by remember { mutableStateOf(projectDefaultName) }

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

    val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.let { data ->
                val results: Bundle = RemoteInput.getResultsFromIntent(data)
                val newInputText: CharSequence? = results.getCharSequence(inputTextKey)
                projectName = newInputText as String
            }
        }

    RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
    Column(modifier = modifier.padding(horizontal = Dimen.spacing)) {
        Row(modifier = Modifier.weight(1f)) {
            // TODO - color picker
        }
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier) {
            Text(text = projectName, Modifier.weight(1f))
            Button(
                onClick = { launcher.launch(intent) },
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(id = R.string.edit_project_name)
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Dimen.spacing, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth().weight(1f)) {
            Button(
                onClick = { onSave.invoke(projectName) },
                colors = ButtonDefaults.primaryButtonColors()
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(id = R.string.save_new_project)
                )
            }
            Button(
                onClick = { onCancel.invoke() },
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(id = R.string.cancel_new_project)
                )
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun AddProjectContentPreview() {
    AddProjectContent(3, {}, {})
}
