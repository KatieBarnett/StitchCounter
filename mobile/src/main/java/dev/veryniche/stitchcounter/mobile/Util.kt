package dev.veryniche.stitchcounter.mobile

import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

fun <T : Any> snapshotStateListSaver() = listSaver<SnapshotStateList<T>, T>(
    save = { stateList -> stateList.toList() },
    restore = { it.toMutableStateList() },
)