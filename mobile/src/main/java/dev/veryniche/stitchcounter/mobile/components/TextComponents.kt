package dev.veryniche.stitchcounter.mobile.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.veryniche.stitchcounter.core.theme.Dimen

@Composable
fun Heading(textRes: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = textRes),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun InfoText(textRes: Int, modifier: Modifier = Modifier, bold: Boolean = false) {
    Text(
        text = stringResource(id = textRes),
        fontWeight = if (bold) {
            FontWeight.Bold
        } else {
            null
        },
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun OptionText(textRes: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(textRes),
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier.padding(start = 16.dp)
    )
}

@Composable
fun UnorderedListText(textLines: List<Int>, modifier: Modifier = Modifier) {
    val bullet = "\u2022"
    val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = Dimen.bulletTextIndent))
    Text(
        buildAnnotatedString {
            textLines.forEach {
                val string = stringResource(id = it)
                withStyle(style = paragraphStyle) {
                    append(bullet)
                    append("\t")
                    append(string)
                }
            }
        },
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier.fillMaxWidth()
    )
}
