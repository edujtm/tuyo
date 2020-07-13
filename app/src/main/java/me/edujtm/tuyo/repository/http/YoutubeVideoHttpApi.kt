package me.edujtm.tuyo.repository.http

import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.edujtm.tuyo.auth.CredentialFactory
import javax.inject.Inject


class YoutubeVideoHttpApi
    @Inject constructor(val credentials: CredentialFactory) : VideoHttpApi {

    /**
     * Made this because the user email necessary for the YouTube class
     * has a different lifetime than the repository, so it cannot be constructor injected.
     * The email is only available after the user logs in, but the repository might be created
     * before that.
     */
    private val youtube: YouTube
        get() {
            val credential = credentials.currentUser()
            val transport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()

            return YouTube.Builder(transport, jsonFactory, credential)
                .build()
        }

    override suspend fun getVideoInfo(): RequestState<List<String>> = withContext(Dispatchers.IO) {
        val channelInfo = arrayListOf<String>()
        try {
            val result = youtube.channels().list("snippet,contentDetails,statistics")
                .setForUsername("GoogleDevelopers")
                .execute()

            val channels = result.items
            if (channels != null) {
                val channel = channels[0]
                channelInfo.add("""
                    |This channel's ID is ${channel.id}.
                    |Its title is ${channel.snippet.title},
                    |and it has ${channel.statistics.viewCount} views.
                """.trimMargin())
            }
            RequestState.Success(channelInfo)
        } catch (e: Exception) {
             RequestState.Failure(e)
        }
    }

}

