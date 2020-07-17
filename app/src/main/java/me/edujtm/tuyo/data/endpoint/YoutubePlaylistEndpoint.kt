package me.edujtm.tuyo.data.endpoint

import com.google.api.services.youtube.YouTube
import me.edujtm.tuyo.data.PlaylistItem
import javax.inject.Inject

class YoutubePlaylistEndpoint
    @Inject constructor(val youtube: YouTube) : PlaylistEndpoint {

    override fun getPlaylistById(id: String): List<PlaylistItem> = youtube.playlistItems()
        .list("snippet,contentDetails")
        .apply {
            maxResults = 25
            playlistId = id
        }.execute()
        .items.map { PlaylistItem.fromJson(it) }
}