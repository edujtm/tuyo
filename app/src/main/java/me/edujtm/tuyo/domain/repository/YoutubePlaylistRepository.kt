package me.edujtm.tuyo.domain.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.edujtm.tuyo.data.PlaylistItem
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.domain.domainmodel.RequestState
import javax.inject.Inject

class YoutubePlaylistRepository
@Inject constructor(
    val playlistEndpoint: PlaylistEndpoint,
    val userEndpoint: UserEndpoint
) : PlaylistRepository {

    override suspend fun getLikedVideos(): RequestState<List<PlaylistItem>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val primaryPlaylists = userEndpoint.getPrimaryPlaylistsIds()
            val playlistItems = playlistEndpoint.getPlaylistById(primaryPlaylists.likedVideos)
            RequestState.Success(playlistItems)
        } catch (e: Exception) {
            RequestState.Failure(e)
        }
    }
}