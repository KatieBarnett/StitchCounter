package dev.katiebarnett.stitchcounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.katiebarnett.stitchcounter.data.models.Counter
import dev.katiebarnett.stitchcounter.data.models.Project
import dev.katiebarnett.stitchcounter.storage.ProjectsRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedProjectsRepository: ProjectsRepository
) : ViewModel() {

    private val _pageContext = MutableLiveData("")
    val pageContext: LiveData<String> = _pageContext
    
    val projects = savedProjectsRepository.getProjects()

    fun getProject(id: Int) = savedProjectsRepository.getProject(id).filterNotNull()

    fun saveProject(projectName: String) {
        saveProject(null, projectName)
    }

    fun saveProject(projectId: Int?, projectName: String) {
        viewModelScope.launch {
            savedProjectsRepository.saveProject(
                Project(
                    id = projectId, 
                    name = projectName
                )
            )
        }
    }

    fun saveProject(project: Project) {
        viewModelScope.launch {
            savedProjectsRepository.saveProject(project)
        }
    }

    fun saveCounter(projectId: Int, counterId: Int, counterName: String, counterMax: Int) {
        saveCounter(projectId, Counter(
            id = counterId,
            name = counterName,
            maxCount = counterMax
        ))
    }

    fun saveCounter(projectId: Int, counter: Counter) {
        viewModelScope.launch {
            savedProjectsRepository.saveCounter(projectId, counter)
        }
    }
    
    fun updateCounter(project: Project, counter: Counter) {
        val counterIndex = project.counters.indexOfFirst { it.id == counter.id }
        if (counterIndex != -1) {
            val updatedList = project.counters.toMutableList()
            updatedList[counterIndex] = counter
            saveProject(project.copy(counters = updatedList))
        }
    }

    fun resetProject(project: Project) {
        val updatedList = project.counters.map { 
            it.copy(currentCount = 0)
        }
        saveProject(project.copy(counters = updatedList))
    }
    
    fun resetCounter(project: Project, counter: Counter) {
        updateCounter(project, counter.copy(currentCount = 0))
    }

    fun deleteProject(projectId: Int) {
        viewModelScope.launch {
            savedProjectsRepository.deleteProject(projectId)
        }
    }

    fun deleteCounter(projectId: Int, counterId: Int) {
        viewModelScope.launch {
            getProject(projectId).collectLatest { project ->
                val counterIndex = project.counters.indexOfFirst { it.id == counterId }
                if (counterIndex != -1) {
                    val updatedList = project.counters.toMutableList()
                    updatedList.removeAt(counterIndex)
                    saveProject(project.copy(counters = updatedList))
                }
            }
        }
    }

    fun updatePageContext(text: String) {
        _pageContext.postValue(text)
    }
}