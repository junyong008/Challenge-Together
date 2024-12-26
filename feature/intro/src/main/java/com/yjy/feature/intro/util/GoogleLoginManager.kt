package com.yjy.feature.intro.util

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.yjy.feature.intro.BuildConfig.GOOGLE_WEB_CLIENT_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

object GoogleLoginManager {
    fun login(
        context: Context,
        onSuccess: (googleId: String) -> Unit,
        onFailure: () -> Unit,
    ) {
        val credentialManager = CredentialManager.create(context)
        val request = createGoogleCredentialRequest()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                handleCredentialResult(
                    result = credentialManager.getCredential(
                        request = request,
                        context = context as ComponentActivity,
                    ),
                    onSuccess = onSuccess,
                    onFailure = onFailure,
                )
            } catch (e: GetCredentialCancellationException) {
                Timber.e(e, "User cancelled the login")
            } catch (e: GetCredentialException) {
                Timber.e(e, "Failed to get credential")
                onFailure()
            }
        }
    }

    private fun createGoogleCredentialRequest(): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(GOOGLE_WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    private fun handleCredentialResult(
        result: GetCredentialResponse,
        onSuccess: (String) -> Unit,
        onFailure: () -> Unit,
    ) {
        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                onSuccess(googleIdTokenCredential.idToken)
            } catch (e: GoogleIdTokenParsingException) {
                Timber.e(e, "Failed to parse Google ID token")
                onFailure()
            }
        } else {
            onFailure()
        }
    }
}
