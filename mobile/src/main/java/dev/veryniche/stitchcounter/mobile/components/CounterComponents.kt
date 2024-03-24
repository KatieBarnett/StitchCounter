package dev.veryniche.stitchcounter.mobile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.mobile.previews.PreviewComponent
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@Composable
fun CounterListItemComponent(
    counter: Counter,
    onCounterUpdate: (counter: Counter) -> Unit,
    onCounterClick: (counter: Counter) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimen.spacingDouble),
            modifier = Modifier.padding(Dimen.spacingTriple)
        ) {
            OutlinedButton(
                onClick = {
                    if (counter.currentCount > 0) {
                        onCounterUpdate.invoke(counter.copy(currentCount = counter.currentCount - 1))
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = stringResource(id = R.string.counter_subtract)
                )
            }
            CounterCentre(
                name = counter.name,
                displayedCount = if (counter.maxCount == 0) {
                    stringResource(R.string.counter_fraction_zero, counter.currentCount)
                } else {
                    stringResource(
                        R.string.counter_fraction_many,
                        counter.currentCount,
                        counter.maxCount
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .clickable { onCounterClick.invoke(counter) },
            )
            Button(
                onClick = { onCounterUpdate.invoke(counter.copy(currentCount = counter.currentCount + 1)) },
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.counter_add)
                )
            }
        }
    }
}

@Composable
fun CounterCentre(
    displayedCount: String,
    name: String,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(text = displayedCount, textAlign = TextAlign.Center)
        Text(text = name, textAlign = TextAlign.Center)
    }
}

//@PreviewComponent
//@Composable
//fun CounterCentrePreview() {
//    StitchCounterTheme {
//        CounterCentre(name = "pattern", displayedCount = "4")
//    }
//}
//
//@PreviewComponent
//@Composable
//fun CounterCentreWithMaxPreview() {
//    StitchCounterTheme {
//        CounterCentre(name = "pattern", displayedCount = "4/70")
//    }
//}

@PreviewComponent
@Composable
fun CounterPreview() {
    StitchCounterTheme {
        CounterListItemComponent(Counter(id = 3, name = "pattern", currentCount = 4), {}, {})
    }
}

@PreviewComponent
@Composable
fun CounterLongTextPreview() {
    StitchCounterTheme {
        CounterListItemComponent(
            counter = Counter(
                id = 3,
                name = "pattern that is something super long",
                currentCount = 4000,
                maxCount = 1000
            ),
            onCounterUpdate = {},
            onCounterClick = {}
        )
    }
}
