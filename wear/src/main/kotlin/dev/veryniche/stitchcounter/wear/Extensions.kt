package dev.veryniche.stitchcounter.wear

import dev.veryniche.mobile.data.models.Counter
import dev.veryniche.mobile.data.models.Project

fun Counter.getCounterProgress(): Float? {
    return if (maxCount > 0) {
        (currentCount.toFloat() / maxCount).coerceAtLeast(0f).coerceAtMost(1f)
    } else {
        null
    }
}

fun Project.getNextCounterId() = (counters.maxByOrNull { it.id }?.id ?: -1) + 1