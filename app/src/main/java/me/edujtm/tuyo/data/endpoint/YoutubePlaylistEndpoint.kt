package me.edujtm.tuyo.data.endpoint

import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.PlaylistItemListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.edujtm.tuyo.data.model.PlaylistItem
import javax.inject.Inject

class YoutubePlaylistEndpoint
    @Inject constructor(val youtube: YouTube) : PlaylistEndpoint {

    // TODO: maybe move the withContext() to where it's being called
    override suspend fun getPlaylistById(
        id: String,
        token: String?,
        pageSize: Long
    ): PlaylistItemListResponse = withContext(Dispatchers.IO) {
        youtube.playlistItems()
            .list("snippet,contentDetails")
            .apply {
                maxResults = pageSize
                playlistId = id
                pageToken = token
            }.execute()
    }
}