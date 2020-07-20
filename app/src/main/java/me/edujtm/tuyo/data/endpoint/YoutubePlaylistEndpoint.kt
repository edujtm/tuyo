package me.edujtm.tuyo.data.endpoint

import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.PlaylistItemListResponse
import me.edujtm.tuyo.data.model.PlaylistItem
import javax.inject.Inject

class YoutubePlaylistEndpoint
    @Inject constructor(val youtube: YouTube) : PlaylistEndpoint {

    override fun getPlaylistById(id: String): PlaylistItemListResponse = youtube.playlistItems()
        .list("snippet,contentDetails")
        .apply {
            maxResults = 25
            playlistId = id
        }.execute()
}