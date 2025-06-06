package dev.veryniche.stitchcounter.mobile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import dev.veryniche.stitchcounter.core.Analytics.Action
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.core.trackEvent
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.mobile.previews.PreviewComponent
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CounterListItemComponent(
    counter: Counter,
    onCounterUpdate: (counter: Counter) -> Unit,
    onCounterDelete: () -> Unit,
    inEditMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var counterName by rememberSaveable { mutableStateOf(counter.name) }
    var counterMaxCount by rememberSaveable { mutableStateOf<String>(counter.maxCount.toString()) }
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        border = if (inEditMode) {
            BorderStroke(width = Dp.Hairline, color = MaterialTheme.colorScheme.onBackground)
        } else {
            null
        }
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
                    enabled = !inEditMode,
                    modifier = Modifier.width(Dimen.mobileListCounterButtonWidth).aspectRatio(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = stringResource(id = R.string.counter_subtract)
                    )
                }
                CounterCentre(
                    name = counter.name,
                    currentCount = counter.currentCount,
                    maxCount = counter.maxCount,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    enabled = !inEditMode,
                    modifier = Modifier.width(Dimen.mobileListCounterButtonWidth).aspectRatio(1f),
                    onClick = {
                        if (counter.maxCount > 0 && counter.currentCount >= counter.maxCount) {
                            onCounterUpdate.invoke(counter.copy(currentCount = 1))
                        } else {
                            onCounterUpdate.invoke(counter.copy(currentCount = counter.currentCount + 1))
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.counter_add)
                    )
                }
            }
            if (inEditMode) {
                Surface {
                    Column(Modifier.padding(Dimen.spacingTriple)) {
                        OutlinedTextField(
                            value = counterName,
                            onValueChange = { counterName = it },
                            isError = counterName.isBlank(),
                            singleLine = true,
                            label = {
                                Text(
                                    stringResource(dev.veryniche.stitchcounter.mobile.R.string.label_counter_name)
                                )
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
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = { keyboardController?.hide() }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = counterMaxCount.toString(),
                            isError = counterMaxCount.trim().toIntOrNull() == null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            onValueChange = { counterMaxCount = it },
                            supportingText = {
                                if (counterMaxCount.trim().toIntOrNull() == null) {
                                    Text(
                                        text = stringResource(
                                            dev.veryniche.stitchcounter.mobile.R.string.validation_message_counter_max_count
                                        ),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            label = {
                                Text(
                                    stringResource(
                                        dev.veryniche.stitchcounter.mobile.R.string.label_counter_max_count
                                    )
                                )
                            },
                            keyboardActions = KeyboardActions(
                                onDone = { keyboardController?.hide() }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(Dimen.spacingTriple)
                        ) {
                            Button(
                                onClick = {
                                    val updatedCounter = counter.copy(
                                        name = counterName.trim(),
                                        maxCount = counterMaxCount.trim().toIntOrNull() ?: 0
                                    )
                                    onCounterUpdate.invoke(updatedCounter)
                                    counterMaxCount = updatedCounter.maxCount.toString()
                                },
                                colors = ButtonDefaults.buttonColors(),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = stringResource(id = R.string.save_counter)
                                )
                                Text(stringResource(dev.veryniche.stitchcounter.mobile.R.string.save_counter_short))
                            }
                            Button(
                                onClick = {
                                    trackEvent(Action.ResetCounter, isMobile = true)
                                    onCounterUpdate.invoke(counter.copy(currentCount = 0))
                                },
                                colors = ButtonDefaults.filledTonalButtonColors()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = stringResource(id = R.string.reset_counter)
                                )
                                Text(stringResource(dev.veryniche.stitchcounter.mobile.R.string.reset_counter_short))
                            }
                            Button(
                                onClick = {
                                    trackEvent(Action.DeleteCounter, isMobile = true)
                                    onCounterDelete.invoke()
                                },
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
    currentCount: Int,
    maxCount: Int,
    name: String,
    modifier: Modifier = Modifier
) {
    var showMaxCount by remember(maxCount) { mutableStateOf(maxCount > 0) }
    val displayedCount = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.displayMedium.fontSize
            )
        ) {
            append(currentCount.toString())
        }
        if (showMaxCount) {
            withStyle(
                style = SpanStyle(
                    fontSize = MaterialTheme.typography.displayMedium.fontSize / 2
                )
            ) {
                append("/")
                append(maxCount.toString())
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(
            text = displayedCount,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displaySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { result ->
                showMaxCount = showMaxCount && !result.hasVisualOverflow
            },
        )
        Text(
            text = name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
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
            onCounterUpdate = {},
            onCounterDelete = {}
        )
    }
}

@PreviewComponent
@Composable
fun CounterVeryLongTextPreview() {
    StitchCounterTheme {
        CounterListItemComponent(
            counter = Counter(
                id = 3,
                name = "pattern that is something super long",
                currentCount = 400000,
                maxCount = 100000
            ),
            onCounterUpdate = {},
            onCounterDelete = {}
        )
    }
}
