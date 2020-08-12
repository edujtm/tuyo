package me.edujtm.tuyo.data.endpoint

import com.google.api.services.youtube.YouTube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.edujtm.tuyo.data.model.PlaylistHeaderJson
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds
import me.edujtm.tuyo.domain.domainmodel.PagedData
import javax.inject.Inject

class YoutubeUserEndpoint
    @Inject constructor(val youtube: YouTube) : UserEndpoint {

    // TODO: write this imperatively
    override suspend fun getPrimaryPlaylistsIds(): PrimaryPlaylistsIds =
        withContext(Dispatchers.IO) {
            youtube.channels()
                .list("snippet,contentDetails,statistics")
                .apply {
                    mine = true
                }.execute()
                .items
                .firstOrNull()?.let {
                    val playlists = it.contentDetails.relatedPlaylists
                    PrimaryPlaylistsIds(
                        likedVideos = playlists.likes,
                        favorites = playlists.favorites,
                        watchLater = playlists.watchLater,
                        history = playlists.watchHistory
                    )
                } ?: throw Exception("Couldn't retrieve primary user playlists")
        }

    override suspend fun getUserPlaylists(token: String?): PagedData<List<PlaylistHeaderJson>, String?> =
        withContext(Dispatchers.IO) {
            val result = youtube.playlists()
                .list("snippet,contentDetails")
                .apply {
                    mine = true
                    pageToken = token
                    maxResults = 40L
                }.execute()

            val headers = result.items.map { PlaylistHeaderJson.fromYoutubeModel(it, result.nextPageToken) }

            return@withContext PagedData(
                headers,
                prevPageToken = result.prevPageToken,
                nextPageToken = result.nextPageToken
            )
        }
}