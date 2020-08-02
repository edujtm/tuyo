package me.edujtm.tuyo.ui.playlistitems

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.data.model.PrimaryPlaylist
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds
import me.edujtm.tuyo.domain.repository.PlaylistRepository
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@FlowPreview
class PlaylistItemsViewModel
    @Inject constructor(
        val playlistRepository: PlaylistRepository
    ) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    fun requestPlaylistItems(playlistId: String, pageToken: String? = null) =
        launch {
            playlistRepository.requestPlaylistItems(playlistId, pageToken)
        }

    fun requestPlaylistItems(primaryPlaylist: PrimaryPlaylist, pageToken: String? = null) =
        launch {
            val playlistIds = playlistRepository.getPrimaryPlaylistsIds()
            val playlistId = playlistIds.selectPlaylist(primaryPlaylist)
            playlistRepository.requestPlaylistItems(playlistId, pageToken)
        }

    fun getPlaylist(playlistId: String) = playlistRepository.getPlaylist(playlistId)

    fun getPrimaryPlaylist(playlist: PrimaryPlaylist) =
        flow { emit(playlistRepository.getPrimaryPlaylistsIds()) }
            .flowOn(Dispatchers.IO)
            .flatMapConcat { playlistIds ->
                val playlistId = playlistIds.selectPlaylist(playlist)
                playlistRepository.getPlaylist(playlistId)
            }

    private fun PrimaryPlaylistsIds.selectPlaylist(primaryPlaylist: PrimaryPlaylist) : String {
        return when (primaryPlaylist) {
            PrimaryPlaylist.FAVORITES -> favorites
            PrimaryPlaylist.LIKED_VIDEOS -> likedVideos
            PrimaryPlaylist.WATCH_LATER -> watchLater
            PrimaryPlaylist.WATCH_HISTORY -> history
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}