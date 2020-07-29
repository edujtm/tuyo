package me.edujtm.tuyo.domain.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.model.PrimaryPlaylist
import me.edujtm.tuyo.domain.paging.PageSource
import javax.inject.Inject

class YoutubePlaylistRepository<T>
@Inject constructor(
    val userEndpoint: UserEndpoint,
    val playlistPager: PageSource<String, T>
) : PlaylistRepository<T> {

    override fun getPlaylist(playlistId: String): Flow<T> {
        return playlistPager.getPages(playlistId)
    }

    @FlowPreview
    override fun getPrimaryPlaylist(primaryPlaylist: PrimaryPlaylist) =
        // TODO: inject dispatchers so Im able to test properly
        flow {
            emit(userEndpoint.getPrimaryPlaylistsIds())
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
}