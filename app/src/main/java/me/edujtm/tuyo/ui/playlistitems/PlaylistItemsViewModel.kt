package me.edujtm.tuyo.ui.playlistitems

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import me.edujtm.tuyo.data.model.*
import me.edujtm.tuyo.domain.DispatcherProvider
import me.edujtm.tuyo.domain.domainmodel.PlaylistItem
import me.edujtm.tuyo.domain.repository.PlaylistRepository
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@FlowPreview
class PlaylistItemsViewModel
    @Inject constructor(
        val playlistRepository: PlaylistRepository,
        val dispatchers: DispatcherProvider
    ) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + dispatchers.main

    /** Works as a memory cache for the values from the DB */
    private val _playlistItems = MutableStateFlow(emptyList<PlaylistItem>())
    val playlistItems: StateFlow<List<PlaylistItem>>
        get() =_playlistItems

    fun requestPlaylistItems(selectedPlaylist: SelectedPlaylist, pageToken: String? = null) =
        launch {
            when (selectedPlaylist) {
                is SelectedPlaylist.Primary -> {
                    val playlistIds = playlistRepository.getPrimaryPlaylistsIds()
                    val playlistId = playlistIds.selectPlaylist(selectedPlaylist.playlist)
                    playlistRepository.requestPlaylistItems(playlistId, pageToken)
                }
                is SelectedPlaylist.Extra -> playlistRepository.requestPlaylistItems(selectedPlaylist.playlistId, pageToken)
            }
        }

    suspend fun getPlaylist(selectedPlaylist: SelectedPlaylist) {
        val playlistFlow = when (selectedPlaylist) {
            is SelectedPlaylist.Primary -> getPrimaryPlaylistFlow(selectedPlaylist.playlist)
            is SelectedPlaylist.Extra -> getPlaylistFlow(selectedPlaylist.playlistId)
        }

        playlistFlow.collect { dbItems ->
            if (dbItems.isEmpty()) {
                requestPlaylistItems(selectedPlaylist)
            }
            _playlistItems.value = dbItems
        }
    }

    fun getPlaylistFlow(playistId: String) = playlistRepository.getPlaylist(playistId)

    fun getPrimaryPlaylistFlow(playlist: PrimaryPlaylist) =
        flow { emit(playlistRepository.getPrimaryPlaylistsIds()) }
            .flowOn(dispatchers.io)
            .flatMapConcat { playlistIds ->
                val playlistId = playlistIds.selectPlaylist(playlist)
                playlistRepository.getPlaylist(playlistId)
            }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}