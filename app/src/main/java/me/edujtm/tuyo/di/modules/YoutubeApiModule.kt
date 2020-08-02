package me.edujtm.tuyo.di.modules

import android.content.Context
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeScopes
import dagger.Binds
import dagger.Module
import dagger.Provides
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.endpoint.YoutubePlaylistEndpoint
import me.edujtm.tuyo.data.endpoint.YoutubeUserEndpoint
import me.edujtm.tuyo.di.qualifier.UserEmail
import me.edujtm.tuyo.domain.repository.PlaylistRepository
import me.edujtm.tuyo.domain.repository.YoutubePlaylistRepository

@Module
abstract class YoutubeApiModule {

    @Binds
    abstract fun provideUserEndpoint(youtubeUserEndpoint: YoutubeUserEndpoint): UserEndpoint

    @Binds
    abstract fun providePlaylistEndpoint(
        repository: YoutubePlaylistEndpoint
    ): PlaylistEndpoint

    @Binds
    abstract fun providePlaylistRepository(
        repository: YoutubePlaylistRepository
    ): PlaylistRepository

    companion object {

        @JvmStatic
        @Provides
        fun provideYouTube(credential: GoogleAccountCredential): YouTube {
            val transport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()

            return YouTube.Builder(transport, jsonFactory, credential).build()
        }

        @JvmStatic
        @Provides
        fun provideGoogleCredentials(
            @UserEmail userEmail: String,
            appContext: Context
        ): GoogleAccountCredential {
            val scopes = listOf(YouTubeScopes.YOUTUBE_READONLY)

            return GoogleAccountCredential.usingOAuth2(appContext, scopes).apply {
                backOff = ExponentialBackOff()
                selectedAccountName = userEmail
            }
        }
    }
}