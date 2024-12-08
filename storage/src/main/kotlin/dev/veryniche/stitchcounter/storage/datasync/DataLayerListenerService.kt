package dev.veryniche.stitchcounter.storage.datasync

import android.annotation.SuppressLint
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import dagger.hilt.android.AndroidEntryPoint
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.storage.ProjectsRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

@AndroidEntryPoint
class DataLayerListenerService : WearableListenerService() {

    @Inject
    lateinit var savedProjectsRepository: ProjectsRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @SuppressLint("VisibleForTests")
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)

        dataEvents.forEach { dataEvent ->
            val frozenEvent = dataEvent.freeze()
            val uri = frozenEvent.dataItem.uri
            Timber.d("Data received: $uri")
            try {
                scope.launch {
                    when (uri.path) {
                        PROJECT_UPDATE_PATH -> {
                            when (frozenEvent.type) {
                                DataEvent.TYPE_CHANGED -> {
                                    val dataString = DataMapItem.fromDataItem(frozenEvent.dataItem)
                                        .dataMap
                                        .getString(KEY_PROJECT)
                                    if (dataString != null) {
                                        val syncedProject = Json.Default.decodeFromString<Project>(dataString)
                                        if (syncedProject.id == null) {
                                            Timber.e("Error - project id is null")
                                        }
                                        savedProjectsRepository.syncProject(syncedProject)
                                        Timber.d("Updated data: $dataString")
                                    } else {
                                        Timber.e("Data sync fail because it is null")
                                    }
                                }
                                else -> {
                                    Timber.e("Data sync fail because the type is unknown")
                                }
                            }
                        }
                        PROJECT_DELETE_PATH -> {
                            when (frozenEvent.type) {
                                DataEvent.TYPE_CHANGED -> {
                                    val dataString = DataMapItem.fromDataItem(frozenEvent.dataItem)
                                        .dataMap
                                        .getString(KEY_PROJECT_ID)
                                    val projectId = dataString?.toIntOrNull()
                                    if (projectId != null) {
                                        savedProjectsRepository.deleteProject(projectId)
                                        Timber.d("Deleted project: $projectId")
                                    } else {
                                        Timber.e("Data sync fail because it is null or not an int")
                                    }
                                }
                                else -> {
                                    Timber.e("Data sync fail because the type is unknown")
                                }
                            }
                        }
                        ALL_PROJECT_SYNC_PATH -> {
                            when (frozenEvent.type) {
                                DataEvent.TYPE_CHANGED -> {
                                    val dataString = DataMapItem.fromDataItem(frozenEvent.dataItem)
                                        .dataMap
                                        .getString(KEY_PROJECTS)
                                    if (dataString != null) {
                                        val syncedProjectList = Json.Default.decodeFromString<List<Project>>(dataString)
                                        savedProjectsRepository.syncAllProjects(syncedProjectList)
                                    } else {
                                        Timber.e("Data sync fail because it is null")
                                    }
                                }
                                else -> {
                                    Timber.e("Data sync fail because the type is unknown")
                                }
                            }
                        }
                    }
                }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                Timber.e(exception, "Data sync fail")
            }
        }
    }

    //    // When the message to start the Wearable app is received, this method starts the Wearable app.
//    // Alternative to this implementation, Horologist offers a DataHelper API which allows to
//    // start the main activity or a different activity of your choice from the Wearable app
//    // see https://google.github.io/horologist/datalayer-helpers-guide/#launching-a-specific-activity-on-the-other-device
//    // for details
//    override fun onMessageReceived(messageEvent: MessageEvent) {
//        super.onMessageReceived(messageEvent)
//
//        when (messageEvent.path) {
//            START_ACTIVITY_PATH -> {
//                startActivity(
//                    Intent(this, MainActivity::class.java)
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                )
//            }
//        }
//    }
//
    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        const val PROJECT_UPDATE_PATH = "/projectUpdate"
        const val PROJECT_DELETE_PATH = "/projectDelete"
        const val ALL_PROJECT_SYNC_PATH = "/allProjectSync"
        const val KEY_PROJECTS = "projects"
        const val KEY_PROJECT = "project"
        const val KEY_PROJECT_ID = "projectId"
    }
}
