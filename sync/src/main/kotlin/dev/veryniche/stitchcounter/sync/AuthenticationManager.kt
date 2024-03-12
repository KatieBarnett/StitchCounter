package dev.veryniche.stitchcounter.sync

import android.content.Context
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import timber.log.Timber
import javax.inject.Singleton

class AuthenticationManager(private val context: Context) {

    companion object {
        private const val SERVER_CLIENT_ID =
            "203037746162-6aj2ui5m5nva8c74b7on6svrbbh0mj8i.apps.googleusercontent.com"
    }

    private var credentialManager: CredentialManager = CredentialManager.create(context)

    suspend fun initAuth(setFilterByAuthorizedAccounts: Boolean = true) {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(setFilterByAuthorizedAccounts) // we will talk about this later
            .setServerClientId(SERVER_CLIENT_ID) // Check point no. 9 & 10
            .setAutoSelectEnabled(true)
//            .setNonce(NONCE) // TODO?
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )
            handleSignIn(result)
        } catch (e: GetCredentialException) {
//            handleFailure(e)
            Timber.e(e, "Error")
        }
    }

    fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        val credential = result.credential

        when (credential) {

            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        // Send googleIdTokenCredential to your server for validation and authentication
                    } catch (e: GoogleIdTokenParsingException) {
                        Timber.e( "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Timber.e("Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Timber.e("Unexpected type of credential")
            }
        }
    }


}