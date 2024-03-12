package dev.veryniche.stitchcounter.sync

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.FileList
//import com.google.auth.http.HttpCredentialsAdapter
//import com.google.auth.oauth2.GoogleCredentials
import java.io.IOException
import java.util.Arrays

/**
 * Class to demonstrate use-case of list 10 files in the application data folder.
 */
object ListAppData {
    /**
     * list down files in the application data folder.
     *
     * @return list of 10 files.
     */
//    @Throws(IOException::class)
//    fun listAppData(): FileList {
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
//            val files = service.files().list()
//                .setSpaces("appDataFolder")
//                .setFields("nextPageToken, files(id, name)")
//                .setPageSize(10)
//                .execute()
//            for (file in files.files) {
//                System.out.printf(
//                    "Found file: %s (%s)\n",
//                    file.name, file.id
//                )
//            }
//
//            return files
//        } catch (e: GoogleJsonResponseException) {
//            // TODO(developer) - handle error appropriately
//            System.err.println("Unable to list files: " + e.details)
//            throw e
//        }
//    }
}