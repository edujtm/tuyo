package me.edujtm.tuyo.domain.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.data.model.PrimaryPlaylist
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds
import me.edujtm.tuyo.data.persistence.YoutubeDatabase
import me.edujtm.tuyo.domain.paging.PageSource
import javax.inject.Inject

class YoutubePlaylistRepository
@Inject constructor(
    val userEndpoint: UserEndpoint,
    val playlistEndpoint: PlaylistEndpoint,
    val database: YoutubeDatabase
) : PlaylistRepository {

    override suspend fun getPrimaryPlaylistsIds(): PrimaryPlaylistsIds =
        withContext(Dispatchers.IO) {
            return@withContext userEndpoint.getPrimaryPlaylistsIds()
        }

    override suspend fun requestPlaylistItems(playlistId: String, token: String?) =
        withContext(Dispatchers.IO) {
            val result = playlistEndpoint.getPlaylistById(playlistId, token)
            val playlitItems = result.items.map { PlaylistItem.fromJson(it, result.nextPageToken) }
            database.playlistItemDao().insertAll(playlitItems)
        }

    override fun getPlaylist(playlistId: String): Flow<List<PlaylistItem>> {
        return database.playlistItemDao().playlistItemsFlow(playlistId)
    }
}