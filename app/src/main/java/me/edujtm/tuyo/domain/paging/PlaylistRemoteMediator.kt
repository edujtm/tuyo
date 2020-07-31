package me.edujtm.tuyo.domain.paging

import android.util.Log
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


// TODO: this has an infinite loop on prepend operations
@ExperimentalPagingApi
class PlaylistRemoteMediator(
    val playlistId: String,
    val playlistEndpoint: PlaylistEndpoint,
    val youtubeDatabase: YoutubeDatabase
) : RemoteMediator<Int, PlaylistItem>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PlaylistItem>
    ): MediatorResult {
        val pageKey = when (loadType) {
            LoadType.PREPEND -> {
                val remoteKeys = youtubeDatabase.firstRemoteKeys(state)
                    ?: throw InvalidObjectException("previous key cannot be null on a PREPEND paging operation")
                // Remote keys cannot be null on a prepend operation

                // If the previous key is null, it's on the beginning of the list
                if (remoteKeys.prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                remoteKeys.prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = youtubeDatabase.lastRemoteKeys(state)
                    ?: throw InvalidObjectException("next key cannot be null on a APPEND paging operation")
                remoteKeys.nextKey
            }
            LoadType.REFRESH -> {
                val remoteKeys = youtubeDatabase.closestRemoteKeys(state)
                remoteKeys?.nextKey
            }
        }

        return try {
            val result = playlistEndpoint.getPlaylistById(
                playlistId,
                pageKey,
                pageSize = state.config.pageSize.toLong()
            )

            val playlistItems = result.items.map { PlaylistItem.fromJson(it) }
            val keys = playlistItems.map { RemoteKeys(it.id, result.prevPageToken, result.nextPageToken) }
            val endOfPagination = result.nextPageToken == null || result.prevPageToken == null

            youtubeDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    youtubeDatabase.remoteKeysDao().clearKeys()
                    youtubeDatabase.playlistItemDao().deletePlaylist(playlistId)
                }

                youtubeDatabase.playlistItemDao().insertAll(playlistItems)
                youtubeDatabase.remoteKeysDao().insertAll(keys)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPagination)
        } catch (e: Exception) {
            Log.e("PAGING_ERROR", "Error occurred: ${e.message}")
            MediatorResult.Error(e)
        }
    }
}

private suspend fun YoutubeDatabase.lastRemoteKeys(state: PagingState<Int, PlaylistItem>) : RemoteKeys? {
    return state.pages
        .flatMap { it.data }
        .lastOrNull()
        ?.let { playlistItem ->
            remoteKeysDao().getRemoteKeyForId(playlistItem.id)
        }
}

private suspend fun YoutubeDatabase.firstRemoteKeys(state: PagingState<Int, PlaylistItem>) : RemoteKeys? {
    return state.pages
        .firstOrNull { it.data.isNotEmpty() }
        ?.data
        ?.firstOrNull()
        ?.let { playlistItem ->
            remoteKeysDao().getRemoteKeyForId(playlistItem.id)
        }
}

private suspend fun YoutubeDatabase.closestRemoteKeys(state: PagingState<Int, PlaylistItem>): RemoteKeys? {
    return state.anchorPosition?.let { position ->
        state.closestItemToPosition(position)?.id?.let { playlistItemId ->
            remoteKeysDao().getRemoteKeyForId(playlistItemId)
        }
    }
}
