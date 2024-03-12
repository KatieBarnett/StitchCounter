package dev.veryniche.stitchcounter.storage

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import dev.veryniche.mobile.storage.models.Projects
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class ProjectsSerializer @Inject constructor() : Serializer<Projects> {

    override val defaultValue: Projects = Projects.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Projects {
        try {
            // readFrom is already called on the data store background thread
            @Suppress("BlockingMethodInNonBlockingContext")
            return Projects.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Projects, output: OutputStream) =
        // writeTo is already called on the data store background thread
        @Suppress("BlockingMethodInNonBlockingContext")
        t.writeTo(output)
}