package dev.katiebarnett.stitchcounter.presentation

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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import dev.katiebarnett.stitchcounter.R
import dev.katiebarnett.stitchcounter.data.models.Counter
import dev.katiebarnett.stitchcounter.presentation.theme.Dimen

@Composable
fun Counter(counter: Counter,
            onCounterUpdate: (counter: Counter) -> Unit,
            modifier: Modifier = Modifier) {
    Row(horizontalArrangement = Arrangement.spacedBy(Dimen.spacing),
        modifier = modifier) {
        Button(
            onClick = { onCounterUpdate.invoke(counter.copy(currentCount = counter.currentCount + 1)) },
            colors = ButtonDefaults.secondaryButtonColors()
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = stringResource(id = R.string.save_new_project)
            )
        }
        CounterCentre(
            name = counter.name,
            currentCount = counter.currentCount,
            modifier = Modifier.weight(1f),
        )
        Button(
            onClick = { onCounterUpdate.invoke(counter.copy(currentCount = counter.currentCount - 1)) },
            colors = ButtonDefaults.primaryButtonColors()
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(id = R.string.cancel_new_project)
            )
        }
    }
}

@Composable
fun CounterCentre(currentCount: Int,
                  name: String,
                  modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(text = currentCount.toString())
        Text(text = name)
    }

}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun CounterCentrePreview() {
    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
            CounterCentre(name = "pattern", currentCount = 4)
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun CounterPreview() {
    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
        Counter(Counter(id = 3, name = "pattern", currentCount = 4), {})
    }
}