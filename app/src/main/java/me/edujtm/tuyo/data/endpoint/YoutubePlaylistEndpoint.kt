package me.edujtm.tuyo.data.endpoint

import com.google.api.services.youtube.YouTube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.edujtm.tuyo.data.model.PlaylistItemJson
import me.edujtm.tuyo.domain.domainmodel.PagedData
import me.edujtm.tuyo.domain.domainmodel.Token
import javax.inject.Inject

class YoutubePlaylistEndpoint
    @Inject constructor(val youtube: YouTube) : PlaylistEndpoint {

    override suspend fun getPlaylistById(
        id: String,
        token: Token?,
        pageSize: Long
    ): PagedData<PlaylistItemJson> = withContext(Dispatchers.IO) {
        val result = youtube.playlistItems()
            .list("snippet,contentDetails")
            .apply {
                maxResults = pageSize
                playlistId = id
                pageToken = token
            }.execute()

        val data = result.items?.map { PlaylistItemJson.fromYoutubeModel(it, result.nextPageToken) }
            ?: throw IllegalStateException("Couldn't retrieve playlists due to empty results")
        return@withContext PagedData(
            data = data,
            prevPageToken = result.prevPageToken,
            nextPageToken = result.nextPageToken
        )
    }
}