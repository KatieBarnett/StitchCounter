package dev.veryniche.stitchcounter.tiles.counter

import dev.veryniche.stitchcounter.data.models.Counter

data class CounterTileState(
    val counter: Counter,
    val projectName: String
)
