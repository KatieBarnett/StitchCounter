package dev.veryniche.stitchcounter.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import dev.veryniche.stitchcounter.R
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.presentation.theme.Dimen
import dev.veryniche.stitchcounter.presentation.theme.StitchCounterTheme
import dev.veryniche.stitchcounter.previews.PreviewComponent
import dev.veryniche.stitchcounter.previews.PreviewScreen


@Composable
fun CounterListItemComponent(counter: Counter,
                             onCounterUpdate: (counter: Counter) -> Unit,
                             onCounterClick: (counter: Counter) -> Unit,
                             modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimen.spacing),
        modifier = modifier) {
        Button(
            onClick = { 
                if (counter.currentCount > 0) { 
                    onCounterUpdate.invoke(counter.copy(currentCount = counter.currentCount - 1))
                }
            },
            colors = ButtonDefaults.secondaryButtonColors()
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
                stringResource(R.string.counter_fraction_many, counter.currentCount, counter.maxCount)
            },
            modifier = Modifier
                .weight(1f)
                .clickable { onCounterClick.invoke(counter) },
        )
        Button(
            onClick = { onCounterUpdate.invoke(counter.copy(currentCount = counter.currentCount + 1)) },
            colors = ButtonDefaults.primaryButtonColors()
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(id = R.string.counter_add)
            )
        }
    }
}

@Composable
fun CounterCentre(displayedCount: String,
                  name: String,
                  modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(text = displayedCount, textAlign = TextAlign.Center)
        Text(text = name, textAlign = TextAlign.Center)
    }
}

@PreviewComponent
@Composable
fun CounterCentrePreview() {
    StitchCounterTheme {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
                CounterCentre(name = "pattern", displayedCount = "4")
            }
        }
    }
}

@PreviewComponent
@Composable
fun CounterCentreWithMaxPreview() {
    StitchCounterTheme {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
                CounterCentre(name = "pattern", displayedCount = "4/70")
            }
        }
    }
}

@PreviewScreen
@Composable
fun CounterPreview() {
    StitchCounterTheme {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
            CounterListItemComponent(Counter(id = 3, name = "pattern", currentCount = 4), {}, {})
        }
    }
}

@PreviewScreen
@Composable
fun CounterLongTextPreview() {
    StitchCounterTheme {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
            CounterListItemComponent(Counter(id = 3, name = "pattern that is something super long", currentCount = 4000, maxCount = 1000), {}, {})
        }
    }
}