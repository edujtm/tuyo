package me.edujtm.tuyo.domain.repository

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds
import me.edujtm.tuyo.data.persistence.PlaylistItemDao
import me.edujtm.tuyo.domain.DispatcherProvider
import javax.inject.Inject

class YoutubePlaylistRepository
@Inject constructor(
    val userEndpoint: UserEndpoint,
    val playlistEndpoint: PlaylistEndpoint,
    val playlistItemDao: PlaylistItemDao,
    val dispatchers: DispatcherProvider
) : PlaylistRepository {

    override suspend fun getPrimaryPlaylistsIds(): PrimaryPlaylistsIds =
        withContext(dispatchers.io) {
            return@withContext userEndpoint.getPrimaryPlaylistsIds()
        }

    override suspend fun requestPlaylistItems(playlistId: String, token: String?) =
        withContext(dispatchers.io) {
            val result = playlistEndpoint.getPlaylistById(playlistId, token)
            playlistItemDao.insertAll(result.data)
        }

    override fun getPlaylist(playlistId: String): Flow<List<PlaylistItem>> {
        return playlistItemDao.getPlaylistItemsById(playlistId)
    }
}