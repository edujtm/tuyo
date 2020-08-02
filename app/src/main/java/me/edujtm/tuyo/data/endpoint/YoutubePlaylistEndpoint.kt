package me.edujtm.tuyo.data.endpoint

import com.google.api.services.youtube.YouTube
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.domain.domainmodel.PagedData
import javax.inject.Inject

class YoutubePlaylistEndpoint
    @Inject constructor(val youtube: YouTube) : PlaylistEndpoint {

    // TODO: maybe move the withContext() to where it's being called
    override fun getPlaylistById(
        id: String,
        token: String?,
        pageSize: Long
    ): PagedData<List<PlaylistItem>, String?> {
        val result = youtube.playlistItems()
            .list("snippet,contentDetails")
            .apply {
                maxResults = pageSize
                playlistId = id
                pageToken = token
            }.execute()

        val data = result.items.map { PlaylistItem.fromJson(it, result.nextPageToken) }
        return PagedData(
            data = data,
            prevPageToken = result.prevPageToken,
            nextPageToken = result.nextPageToken
        )
    }
}