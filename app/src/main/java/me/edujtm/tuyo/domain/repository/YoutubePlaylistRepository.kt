package me.edujtm.tuyo.domain.repository

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.model.PlaylistItemDB
import me.edujtm.tuyo.data.model.PlaylistItemJson
import me.edujtm.tuyo.domain.domainmodel.PlaylistItem
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds
import me.edujtm.tuyo.data.persistence.PlaylistItemDao
import me.edujtm.tuyo.domain.DispatcherProvider
import me.edujtm.tuyo.domain.Mapper
import me.edujtm.tuyo.domain.domainmodel.Token
import javax.inject.Inject


class YoutubePlaylistRepository
@Inject constructor(
    val playlistEndpoint: PlaylistEndpoint,
    val playlistItemDao: PlaylistItemDao,
    val dispatchers: DispatcherProvider
) : PlaylistRepository {

    override suspend fun requestPlaylistItems(playlistId: String, token: Token?) =
        withContext(dispatchers.io) {
            val result = playlistEndpoint.getPlaylistById(playlistId, token)
            val dbItems = result.data.map(networkToDatabase)
            playlistItemDao.insertAll(dbItems)
        }

    override fun getPlaylist(playlistId: String): Flow<List<PlaylistItem>> {
        return playlistItemDao.getPlaylistItemsById(playlistId)
            .map {  dbItems ->
                dbItems.map(databaseToDomain)
            }
    }

    override suspend fun deletePlaylist(playlistId: String) {
        playlistItemDao.deletePlaylist(playlistId)
    }

    // TODO: handle null cases
    private val networkToDatabase: Mapper<PlaylistItemJson, PlaylistItemDB> = { json ->
        PlaylistItemDB(
            id = json.id,
            title = json.title,
            description = json.description,
            channelId = json.channelId,
            thumbnailUrl = json.thumbnailUrl,
            videoId = json.videoId,
            playlistId = json.playlistId,
            nextPageToken = json.nextPageToken
        )
    }

    private val databaseToDomain: Mapper<PlaylistItemDB, PlaylistItem> = { dbItem ->
        PlaylistItem(
            id = dbItem.id,
            title = dbItem.title,
            description = dbItem.description,
            channelId = dbItem.channelId,
            thumbnailUrl = dbItem.thumbnailUrl,
            videoId = dbItem.videoId,
            playlistId = dbItem.playlistId,
            nextPageToken = dbItem.nextPageToken
        )
    }
}