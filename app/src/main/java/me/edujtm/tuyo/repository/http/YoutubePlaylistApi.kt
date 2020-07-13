package me.edujtm.tuyo.repository.http

import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.edujtm.tuyo.auth.CredentialFactory
import me.edujtm.tuyo.data.PlaylistItem
import javax.inject.Inject

class YoutubePlaylistApi
@Inject constructor(val credentials: CredentialFactory) : PlaylistHttpApi {

    private val youtube: YouTube
        get() {
            val credential = credentials.currentUser()
            val transport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()

            return YouTube.Builder(transport, jsonFactory, credential)
                .build()
        }

    override suspend fun getLikedVideos(): RequestState<List<PlaylistItem>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val likedVideosId = retrieveLikedVideosId()
            val result = youtube.playlistItems()
                .list("snippet,contentDetails")
                .apply {
                    maxResults = 25
                    playlistId = likedVideosId
                }.execute()

            val playlistItems = result.items.map { PlaylistItem.fromJson(it) }
            RequestState.Success(playlistItems)
        } catch (e: Exception) {
            RequestState.Failure(e)
        }
    }

    private suspend fun retrieveLikedVideosId(): String = withContext(Dispatchers.IO) {
        val result = youtube.channels()
            .list("snippet,contentDetails,statistics")
            .apply {
                mine = true
            }.execute()

        val channel = result.items.firstOrNull()
        return@withContext channel?.let {
            it.contentDetails.relatedPlaylists.likes
        } ?: throw Exception("Couldn't retrieve channel information")
    }
}