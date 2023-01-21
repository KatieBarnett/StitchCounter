package dev.veryniche.stitchcounter.storage.di

//import dev.veryniche.stitchcounter.storage.Projectserializer
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.veryniche.stitchcounter.core.AppDispatchers
import dev.veryniche.stitchcounter.core.Dispatcher
import dev.veryniche.stitchcounter.storage.ProjectsDataSource.Companion.PROTO_FILE_NAME
import dev.veryniche.stitchcounter.storage.ProjectsSerializer
import dev.veryniche.stitchcounter.storage.models.Projects
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providesProjectsDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        ProjectsSerializer: ProjectsSerializer
    ): DataStore<Projects> =
        DataStoreFactory.create(
            serializer = ProjectsSerializer,
            scope = CoroutineScope(ioDispatcher + SupervisorJob()),
            migrations = listOf()
        ) {
            context.dataStoreFile(PROTO_FILE_NAME)
        }
}
