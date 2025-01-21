package dev.veryniche.stitchcounter.mobile.whatsnew

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.mobile.R
import dev.veryniche.stitchcounter.mobile.previews.PreviewComponent
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@Composable
fun WhatsNewDialog(
    data: List<WhatsNewData>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss.invoke() },
        title = { Text(stringResource(R.string.whats_new_title)) },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Dimen.spacingQuad)
            ) {
                data.forEach {
                    it.text.forEach {
                        item {
                            Text(
                                text = it.text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss.invoke()
            }) {
                Text(stringResource(R.string.whats_new_close))
            }
        }
    )
}

@PreviewComponent
@Composable
fun WhatsNewDialogPreview() {
    StitchCounterTheme {
        WhatsNewDialog(
            data = whatsNewData,
            onDismiss = {},
        )
    }
}
