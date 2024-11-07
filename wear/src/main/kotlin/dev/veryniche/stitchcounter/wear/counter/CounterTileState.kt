package dev.veryniche.stitchcounter.wear.counter

import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project

data class CounterTileState(
    val counter: Counter? = null,
    val project: Project? = null
)
