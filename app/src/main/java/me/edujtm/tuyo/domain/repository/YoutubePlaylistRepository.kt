package me.edujtm.tuyo.domain.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.model.PrimaryPlaylist
import me.edujtm.tuyo.data.persistence.YoutubeDatabase
import me.edujtm.tuyo.domain.paging.PlaylistRemoteMediator
import javax.inject.Inject

@ExperimentalPagingApi
class YoutubePlaylistRepository
@Inject constructor(
    val playlistEndpoint: PlaylistEndpoint,
    val userEndpoint: UserEndpoint,
    val youtubeDatabase: YoutubeDatabase
) : PlaylistRepository {

    override fun getPlaylist(playlistId: String): Flow<PagingData<PlaylistItem>> {
        val pagingFactory = {
            youtubeDatabase.playlistItemDao().playlistItemsById(playlistId)
        }
        return Pager(
            config = PagingConfig(pageSize = PLAYLIST_PAGE_SIZE),
            remoteMediator = PlaylistRemoteMediator(
                playlistId,
                playlistEndpoint,
                youtubeDatabase
            ),
            pagingSourceFactory = pagingFactory
        ).flow
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun getPrimaryPlaylist(primaryPlaylist: PrimaryPlaylist) =
        flow {
            val playlistIds = userEndpoint.getPrimaryPlaylistsIds()
            emit(playlistIds)
        }
        .flowOn(Dispatchers.IO)
        .flatMapConcat { playlistIds ->
            val selectedPlaylist = when (primaryPlaylist) {
                PrimaryPlaylist.LIKED_VIDEOS -> playlistIds.likedVideos
                PrimaryPlaylist.FAVORITES -> playlistIds.favorites
                PrimaryPlaylist.WATCH_HISTORY -> playlistIds.history
                PrimaryPlaylist.WATCH_LATER -> playlistIds.watchLater
            }

            getPlaylist(selectedPlaylist)
        }

    companion object {
        const val PLAYLIST_PAGE_SIZE = 40
    }
}