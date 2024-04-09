package dev.veryniche.stitchcounter.mobile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.mobile.previews.PreviewComponent
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CounterListItemComponent(
    counter: Counter,
    defaultCounterName: String,
    onCounterUpdate: (counter: Counter) -> Unit,
    onCounterDelete: (counter: Counter) -> Unit,
//    onCounterClick: (counter: Counter) -> Unit,
    inEditMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    var counterName by remember { mutableStateOf(counter.name) }
    var counterMaxCount by remember { mutableStateOf<Int?>(counter.maxCount) }
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    ) {
        Column {
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
                    modifier = Modifier.weight(1f)
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
            if (inEditMode) {
                Surface() {
                    Column {
                        Row(modifier = Modifier.padding(start = Dimen.spacingTriple, end = Dimen.spacingTriple, top = Dimen.spacingTriple)) {
                            OutlinedTextField(
                                value = counterName,
                                onValueChange = { counterName = it.trim() },
                                isError = counterName.isBlank(),
                                label = {
                                    Text(
                                        stringResource(dev.veryniche.stitchcounter.mobile.R.string.label_counter_name)
                                    )
                                },
                                placeholder = {
                                    Text(defaultCounterName)
                                },
                                supportingText = {
                                    if (counterName.isBlank()) {
                                        Text(
                                            text = stringResource(
                                                dev.veryniche.stitchcounter.mobile.R.string.validation_message_counter_name
                                            ),
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Row(
                            modifier = Modifier.padding(
                                start = Dimen.spacingTriple,
                                end = Dimen.spacingTriple,
                                bottom = Dimen.spacingTriple
                            )
                        ) {
                            OutlinedTextField(
                                value = counterMaxCount.toString(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                onValueChange = { counterMaxCount = it.trim().toIntOrNull() },
                                label = {
                                    Text(
                                        stringResource(
                                            dev.veryniche.stitchcounter.mobile.R.string.label_counter_max_count
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(Dimen.spacingTriple),
                            modifier = Modifier.padding(
                                start = Dimen.spacingTriple,
                                end = Dimen.spacingTriple,
                                bottom = Dimen.spacingTriple
                            )
                        ) {
                            Button(
                                onClick = { /*TODO*/ },
                                colors = ButtonDefaults.buttonColors()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = stringResource(id = R.string.save_counter)
                                )
                                Text(stringResource(dev.veryniche.stitchcounter.mobile.R.string.save_counter_short))
                            }
                            Button(
                                onClick = { /*TODO*/ },
                                colors = ButtonDefaults.filledTonalButtonColors()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = stringResource(id = R.string.reset_counter)
                                )
                                Text(stringResource(dev.veryniche.stitchcounter.mobile.R.string.reset_counter_short))
                            }
                            Button(
                                onClick = { /*TODO*/ },
                                colors = ButtonDefaults.filledTonalButtonColors()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = stringResource(id = R.string.delete_counter)
                                )
                                Text(stringResource(dev.veryniche.stitchcounter.mobile.R.string.delete_counter_short))
                            }
                        }
                    }
                }
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

// @PreviewComponent
// @Composable
// fun CounterCentrePreview() {
//    StitchCounterTheme {
//        CounterCentre(name = "pattern", displayedCount = "4")
//    }
// }
//
// @PreviewComponent
// @Composable
// fun CounterCentreWithMaxPreview() {
//    StitchCounterTheme {
//        CounterCentre(name = "pattern", displayedCount = "4/70")
//    }
// }

@PreviewComponent
@Composable
fun CounterPreview() {
    StitchCounterTheme {
        CounterListItemComponent(
            Counter(id = 3, name = "pattern", currentCount = 4),
            defaultCounterName = "Counter 1",
            {},
            {},
        )
    }
}

@PreviewComponent
@Composable
fun CounterEditPreview() {
    StitchCounterTheme {
        CounterListItemComponent(
            Counter(id = 3, name = "pattern", currentCount = 4),
            defaultCounterName = "Counter 1",
            {},
            {},
            inEditMode = true
        )
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
            defaultCounterName = "Counter 1",
            onCounterUpdate = {},
            onCounterDelete = {}
        )
    }
}
