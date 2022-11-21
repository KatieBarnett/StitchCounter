package dev.katiebarnett.stitchcounter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.katiebarnett.stitchcounter.data.models.Counter
import dev.katiebarnett.stitchcounter.storage.ProjectsRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedProjectsRepository: ProjectsRepository
) : ViewModel() {

    val projects = savedProjectsRepository.getProjects()

    fun getProject(id: Int) = savedProjectsRepository.getProject(id)

    fun updateCounter(counter: Counter) {

    }
}