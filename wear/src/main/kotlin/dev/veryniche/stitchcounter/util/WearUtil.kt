package dev.veryniche.stitchcounter.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

suspend fun openUrlOnPhone(context: Context, url: String) {
    val remoteActivityHelper = RemoteActivityHelper(context)
    val nodeClient = Wearable.getNodeClient(context)
    val connectedNodes = nodeClient.connectedNodes.await()
    connectedNodes.forEach { node ->
        remoteActivityHelper.startRemoteActivity(
            Intent(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.parse(url)),
            node.id
        )
    }
}

suspend fun emailOnPhone(context: Context, email: String, emailSubject: String) {
    val remoteActivityHelper = RemoteActivityHelper(context)
    val nodeClient = Wearable.getNodeClient(context)
    val connectedNodes = nodeClient.connectedNodes.await()
    connectedNodes.forEach { node ->
        remoteActivityHelper.startRemoteActivity(
            Intent(Intent.ACTION_SENDTO)
                .putExtra(Intent.EXTRA_EMAIL, email)
                .putExtra(Intent.EXTRA_SUBJECT, emailSubject),
            node.id
        )
    }
}