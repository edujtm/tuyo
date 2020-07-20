package me.edujtm.tuyo.domain.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.persistence.YoutubeDatabase
import me.edujtm.tuyo.domain.domainmodel.RequestState
import me.edujtm.tuyo.domain.paging.PlaylistRemoteMediator
import javax.inject.Inject

@ExperimentalPagingApi
class YoutubePlaylistRepository
@Inject constructor(
    val playlistEndpoint: PlaylistEndpoint,
    val userEndpoint: UserEndpoint,
    val youtubeDatabase: YoutubeDatabase
) : PlaylistRepository {

    override fun getLikedVideos(): Flow<PagingData<PlaylistItem>> {
        val primaryPlaylists = userEndpoint.getPrimaryPlaylistsIds()
        val pagingFactory = {
            youtubeDatabase.playlistItemDao().playlistItemsById(primaryPlaylists.likedVideos)
        }
        return Pager(
            config = PagingConfig(pageSize = PLAYLIST_PAGE_SIZE),
            remoteMediator = PlaylistRemoteMediator(
                primaryPlaylists.likedVideos,
                playlistEndpoint,
                youtubeDatabase
            ),
            pagingSourceFactory = pagingFactory
        ).flow
    }

    companion object {
        const val PLAYLIST_PAGE_SIZE = 40
    }
}