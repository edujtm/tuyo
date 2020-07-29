package me.edujtm.tuyo.domain.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.data.persistence.YoutubeDatabase
import me.edujtm.tuyo.domain.repository.YoutubePlaylistRepository
import javax.inject.Inject

@ExperimentalPagingApi
class PlaylistPageSource
    @Inject constructor(
        val playlistEndpoint: PlaylistEndpoint,
        val youtubeDatabase: YoutubeDatabase
    ) : PageSource<String, PagingData<PlaylistItem>> {

    override fun getPages(query: String) : Flow<PagingData<PlaylistItem>> {
            val pagingFactory = {
                youtubeDatabase.playlistItemDao().playlistItemsById(query)
            }
            return Pager(
                config = PagingConfig(pageSize = PLAYLIST_PAGE_SIZE),
                remoteMediator = PlaylistRemoteMediator(
                    query,
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