package dev.veryniche.mobile.storage

import dev.veryniche.mobile.data.models.Counter
import dev.veryniche.mobile.data.models.Project
import dev.veryniche.mobile.storage.models.SavedCounter
import dev.veryniche.mobile.storage.models.SavedProject

fun SavedProject.fromSavedProject(): Project {
    return Project(
        id = id,
        name = name,
        elapsedTime = elapsedTime.toLong(),
        lastModified = lastModified.toLong(),
        counters = countersList.map { it.fromSavedCounter() }
    )
}

fun SavedCounter.fromSavedCounter(): Counter {
    return Counter(
        id = id,
        name = name,
        currentCount = currentCount,
        maxCount = maxCount
    )
}

fun Project.toSavedProject(): SavedProject {
    val projectBuilder = SavedProject.newBuilder()
    projectBuilder.id = id ?: -1
    projectBuilder.name = name
    projectBuilder.elapsedTime = elapsedTime.toDouble()
    projectBuilder.addAllCounters(counters.map { it.toSavedCounter() })
    projectBuilder.lastModified = lastModified.toDouble()
    return projectBuilder.build()
}

fun Counter.toSavedCounter(): SavedCounter {
    val counterBuilder = SavedCounter.newBuilder()
    counterBuilder.id = id
    counterBuilder.name = name
    counterBuilder.currentCount = currentCount
    counterBuilder.maxCount = maxCount
    return counterBuilder.build()
}


