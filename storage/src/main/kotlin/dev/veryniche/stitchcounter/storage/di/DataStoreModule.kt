package dev.veryniche.stitchcounter.storage.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.veryniche.stitchcounter.data.AppDispatchers
import dev.veryniche.stitchcounter.data.Dispatcher
import dev.veryniche.mobile.storage.ProjectsDataSource.Companion.PROTO_FILE_NAME
import dev.veryniche.stitchcounter.storage.ProjectsSerializer
import dev.veryniche.mobile.storage.models.Projects
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providesProjectsStore(
        @ApplicationContext context: Context,
        @Dispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        projectsSerializer: ProjectsSerializer
    ): DataStore<Projects> =
        DataStoreFactory.create(
            serializer = projectsSerializer,
            scope = CoroutineScope(ioDispatcher + SupervisorJob()),
            migrations = listOf()
        ) {
            context.dataStoreFile(PROTO_FILE_NAME)
        }

}
