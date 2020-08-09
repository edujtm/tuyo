package me.edujtm.tuyo.data.endpoint

import com.google.api.services.youtube.YouTube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds
import javax.inject.Inject

class YoutubeUserEndpoint
    @Inject constructor(val youtube: YouTube) : UserEndpoint {

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

}