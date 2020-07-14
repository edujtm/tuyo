package me.edujtm.tuyo.auth

import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTubeScopes
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CredentialFactory
@Inject constructor(val context: Context, val authManager: AuthManager) {

    private val SCOPES = listOf(YouTubeScopes.YOUTUBE_READONLY)

    fun currentUser() : GoogleAccountCredential {
        return GoogleAccountCredential.usingOAuth2(context, SCOPES)
            .apply {
                backOff = ExponentialBackOff()
                selectedAccountName = authManager.getUserAccount()?.email
            }
    }

}