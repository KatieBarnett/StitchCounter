package dev.veryniche.stitchcounter.presentation.whatsnew

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import dev.veryniche.stitchcounter.R

@Composable
fun WhatsNewDialog(
    data: List<WhatsNewData>,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Alert(
        title = {
            Text(
                text = stringResource(R.string.whats_new_title),
                textAlign = TextAlign.Center
            )
        },
        negativeButton = {},
        positiveButton = {
            Button(
                onClick = onClose,
                colors = ButtonDefaults.primaryButtonColors()
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(R.string.reset_counter_positive)
                )
            }
        },
        modifier
    ) {
        data.forEach {
            it.text.forEach {
                Text(text = it.text)
            }
        }
    }
}
