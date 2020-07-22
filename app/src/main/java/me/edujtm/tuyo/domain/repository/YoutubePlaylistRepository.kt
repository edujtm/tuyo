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
import me.edujtm.tuyo.data.model.PrimaryPlaylists
import me.edujtm.tuyo.data.persistence.YoutubeDatabase
import me.edujtm.tuyo.domain.paging.PlaylistRemoteMediator
import java.lang.IllegalStateException
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
    override fun getPrimaryPlaylist(primaryPlaylists: Int) = flow {
            val playlistIds = userEndpoint.getPrimaryPlaylistsIds()
            emit(playlistIds)
        }
        .flowOn(Dispatchers.IO)
        .flatMapConcat { playlistIds ->
            val selectedPlaylist = when (primaryPlaylists) {
                PrimaryPlaylists.LIKED_VIDEOS -> playlistIds.likedVideos
                PrimaryPlaylists.FAVORITES -> playlistIds.favorites
                PrimaryPlaylists.WATCH_HISTORY -> playlistIds.history
                PrimaryPlaylists.WATCH_LATER -> playlistIds.watchLater
                // Using enum will remove this
                else -> throw IllegalStateException("Invalid ID for primary playlist")
            }

            getPlaylist(selectedPlaylist)
        }

    companion object {
        const val PLAYLIST_PAGE_SIZE = 40
    }
}