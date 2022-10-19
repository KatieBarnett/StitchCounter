package dev.katiebarnett.stitchcounter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.katiebarnett.stitchcounter.models.Counter
import dev.katiebarnett.stitchcounter.models.Project
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
) : ViewModel() {

    val projects = listOf(
        Project(
            id = 0,
            name = "shawl",
            counters = listOf(
                Counter(id = 1, name = "pattern", currentCount = 7),
                Counter(id = 2, name = "plain", currentCount = 8)
            )
        ),
        Project(id = 1, name = "blanket"),
        Project(id = 2, name = "toy")
    )

    val nextProjectId = 3

    fun getProject(id: Int) = projects.firstOrNull() { it.id == id }

    fun updateCounter(counter: Counter) {

    }
}