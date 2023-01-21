package dev.veryniche.stitchcounter

import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project

fun Counter.getCounterProgress(): Float? {
    return if (maxCount > 0) {
        currentCount.toFloat() / maxCount
    } else {
        null
    }
}

fun Project.getNextCounterId() = (counters.maxByOrNull { it.id }?.id ?: -1) + 1