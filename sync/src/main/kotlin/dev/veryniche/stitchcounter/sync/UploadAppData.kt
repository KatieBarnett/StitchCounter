package dev.veryniche.stitchcounter.sync

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
//import com.google.auth.http.HttpCredentialsAdapter
//import com.google.auth.oauth2.GoogleCredentials
import java.io.IOException
import java.util.Arrays

/**
 * Class to demonstrate use-case of create file in the application data folder.
 */
object UploadAppData {
    /**
     * Creates a file in the application data folder.
     *
     * @return Created file's Id.
     */
//    @Throws(IOException::class)
//    fun uploadAppData(): String {
//        /*Load pre-authorized user credentials from the environment.
//        TODO(developer) - See https://developers.google.com/identity for
//        guides on implementing OAuth2 for your application.*/
//        var credentials: GoogleCredentials? = null
//        try {
//            credentials = GoogleCredentials.getApplicationDefault()
//                .createScoped(Arrays.asList(DriveScopes.DRIVE_APPDATA))
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        val requestInitializer: HttpRequestInitializer = HttpCredentialsAdapter(
//            credentials
//        )
//
//        // Build a new authorized API client service.
//        val service = Drive.Builder(
//            NetHttpTransport(),
//            GsonFactory.getDefaultInstance(),
//            requestInitializer
//        )
//            .setApplicationName("Drive samples")
//            .build()
//        try {
//            // File's metadata.
//            val fileMetadata = File()
//            fileMetadata.setName("config.json")
//            fileMetadata.setParents(listOf("appDataFolder"))
//            val filePath = java.io.File("files/config.json")
//            val mediaContent = FileContent("application/json", filePath)
//            val file = service.files().create(fileMetadata, mediaContent)
//                .setFields("id")
//                .execute()
//            println("File ID: " + file.id)
//            return file.id
//        } catch (e: GoogleJsonResponseException) {
//            // TODO(developer) - handle error appropriately
//            System.err.println("Unable to create file: " + e.details)
//            throw e
//        }
//    }
}