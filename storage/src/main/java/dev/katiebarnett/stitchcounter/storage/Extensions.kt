package dev.katiebarnett.stitchcounter.storage

import dev.katiebarnett.stitchcounter.data.models.Counter
import dev.katiebarnett.stitchcounter.data.models.Project
import dev.katiebarnett.stitchcounter.storage.models.SavedCounter
import dev.katiebarnett.stitchcounter.storage.models.SavedProject

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
        currentCount = currentCount
    )
}

fun Project.toSavedProject(): SavedProject {
    val projectBuilder = SavedProject.newBuilder()
    projectBuilder.id = id
    projectBuilder.name = name
    projectBuilder.elapsedTime = elapsedTime.toDouble()
    projectBuilder.countersList.addAll(counters.map { it.toSavedCounter() })
    projectBuilder.lastModified = lastModified.toDouble()
    return projectBuilder.build()
}

fun Counter.toSavedCounter(): SavedCounter {
    val counterBuilder = SavedCounter.newBuilder()
    counterBuilder.id = id
    counterBuilder.name = name
    counterBuilder.currentCount = currentCount
    return counterBuilder.build()
}


