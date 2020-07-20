package me.edujtm.tuyo.domain.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.model.RemoteKeys
import me.edujtm.tuyo.data.persistence.YoutubeDatabase
import java.io.InvalidObjectException


@ExperimentalPagingApi
class PlaylistRemoteMediator(
    val playlistId: String,
    val playlistEndpoint: PlaylistEndpoint,
    val youtubeDatabase: YoutubeDatabase
) : RemoteMediator<String, PlaylistItem>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<String, PlaylistItem>
    ): MediatorResult {
        val loadPage = when (loadType) {
            LoadType.PREPEND -> {
                val remoteKeys = youtubeDatabase.firstRemoteKeys(state)
                if (remoteKeys?.prevKey == null) {
                    throw InvalidObjectException("previous key cannot be null on a PREPEND paging operation")
                }
                remoteKeys.prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = youtubeDatabase.lastRemoteKeys(state)
                if (remoteKeys?.nextKey == null) {
                    throw InvalidObjectException("next key cannot be null on a APPEND paging operation")
                }
                remoteKeys.nextKey
            }
            // Don't do nothing on refresh for now
            LoadType.REFRESH -> return MediatorResult.Success(endOfPaginationReached = true)
        }

        return try {
            val result = playlistEndpoint.getPlaylistById(playlistId)
            val playlistItems = result.items.map { PlaylistItem.fromJson(it) }
            val keys = playlistItems.map { RemoteKeys(it.id, result.prevPageToken, result.nextPageToken) }
            val endOfPagination = result.nextPageToken == null

            youtubeDatabase.withTransaction {
                youtubeDatabase.playlistItemDao().insertAll(playlistItems)
                youtubeDatabase.remoteKeysDao().insertAll(keys)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPagination)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}

private suspend fun YoutubeDatabase.lastRemoteKeys(state: PagingState<String, PlaylistItem>) : RemoteKeys? {
    return state.pages
        .flatMap { it.data }
        .lastOrNull()
        ?.let { playlistItem ->
            remoteKeysDao().getRemoteKeyForId(playlistItem.id)
        }
}

private suspend fun YoutubeDatabase.firstRemoteKeys(state: PagingState<String, PlaylistItem>) : RemoteKeys? {
    return state.pages
        .flatMap { it.data }
        .firstOrNull()
        ?.let { playlistItem ->
            remoteKeysDao().getRemoteKeyForId(playlistItem.id)
        }
}
