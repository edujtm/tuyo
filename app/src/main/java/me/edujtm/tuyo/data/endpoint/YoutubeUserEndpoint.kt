package me.edujtm.tuyo.data.endpoint

import com.google.api.services.youtube.YouTube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.edujtm.tuyo.data.model.PlaylistHeaderJson
import me.edujtm.tuyo.domain.domainmodel.PrimaryPlaylistsIds
import me.edujtm.tuyo.domain.domainmodel.PagedData
import me.edujtm.tuyo.domain.domainmodel.Token
import javax.inject.Inject

class YoutubeUserEndpoint
    @Inject constructor(val youtube: YouTube) : UserEndpoint {

    override suspend fun getPrimaryPlaylistsIds(): PrimaryPlaylistsIds =
        withContext(Dispatchers.IO) {
            val result = youtube.channels()
                .list("snippet,contentDetails,statistics")
                .apply {
                    mine = true
                }.execute()

            val json = result?.items?.firstOrNull() ?:
                throw IllegalStateException("Couldn't retrieve primary playlists ids")

            val playlists = json.contentDetails.relatedPlaylists
            PrimaryPlaylistsIds(
                likedVideos = playlists.likes,
                favorites = playlists.favorites,
                watchLater = playlists.watchLater,
                history = playlists.watchHistory
            )
        }

    override suspend fun getUserPlaylists(token: Token?): PagedData<PlaylistHeaderJson> =
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