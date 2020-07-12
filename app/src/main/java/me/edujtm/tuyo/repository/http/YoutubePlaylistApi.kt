package me.edujtm.tuyo.repository.http

import com.google.api.services.youtube.YouTube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.edujtm.tuyo.data.PlaylistItem
import org.koin.core.KoinComponent

class YoutubePlaylistApi : PlaylistHttpApi, KoinComponent {

    private val youtube: YouTube
        get() = getKoin().get()

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