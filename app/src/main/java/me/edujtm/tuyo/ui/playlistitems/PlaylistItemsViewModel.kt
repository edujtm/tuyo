package me.edujtm.tuyo.ui.playlistitems

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import me.edujtm.tuyo.data.model.*
import me.edujtm.tuyo.domain.DispatcherProvider
import me.edujtm.tuyo.domain.domainmodel.PlaylistItem
import me.edujtm.tuyo.domain.domainmodel.RequestState
import me.edujtm.tuyo.domain.repository.PlaylistRepository
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class PlaylistItemsViewModel
    @Inject constructor(
        val playlistRepository: PlaylistRepository,
        val dispatchers: DispatcherProvider
    ) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + dispatchers.main

    /** Works as a memory cache for the values from the DB */
    private val _playlistItems = MutableStateFlow<RequestState<List<PlaylistItem>>>(RequestState.Loading)
    val playlistItems: StateFlow<RequestState<List<PlaylistItem>>>
        get() =_playlistItems

    fun refresh(selectedPlaylist: SelectedPlaylist) {
        launch {
            val playlistId = when (selectedPlaylist) {
                is SelectedPlaylist.Primary -> {
                    val playistIds = playlistRepository.getPrimaryPlaylistsIds()
                    playistIds.selectPlaylist(selectedPlaylist.playlist)
                }
                is SelectedPlaylist.Extra -> selectedPlaylist.playlistId
            }
            playlistRepository.deletePlaylist(playlistId)
        }
    }

    fun requestPlaylistItems(selectedPlaylist: SelectedPlaylist, pageToken: String? = null) {
        launch {
            try {
                when (selectedPlaylist) {
                    is SelectedPlaylist.Primary -> {
                        val playlistIds = playlistRepository.getPrimaryPlaylistsIds()
                        val playlistId = playlistIds.selectPlaylist(selectedPlaylist.playlist)
                        playlistRepository.requestPlaylistItems(playlistId, pageToken)
                    }
                    is SelectedPlaylist.Extra -> playlistRepository.requestPlaylistItems(
                        selectedPlaylist.playlistId,
                        pageToken
                    )
                }
            } catch (e: Exception) {
                // Error on pagination will be treated as playlist error
                _playlistItems.value = RequestState.Failure(e)
            }
        }
    }

    // TODO: Review error handling for exceptions (specially due to Flow cancellation)
    suspend fun getPlaylist(selectedPlaylist: SelectedPlaylist) {
        // The first request show a loading state
        _playlistItems.value = RequestState.Loading
        try {
            val playlistFlow = when (selectedPlaylist) {
                is SelectedPlaylist.Primary -> getPrimaryPlaylistFlow(selectedPlaylist.playlist)
                is SelectedPlaylist.Extra -> getPlaylistFlow(selectedPlaylist.playlistId)
            }

            // Following requests (i.e. pagination) will not show the loading state
            playlistFlow.collect { dbItems ->
                if (dbItems.isEmpty()) {
                    requestPlaylistItems(selectedPlaylist)
                }
                _playlistItems.value = RequestState.Success(dbItems)
            }
        } catch (e: Exception) {
            _playlistItems.value = RequestState.Failure(e)
        }
    }

    private fun getPlaylistFlow(playistId: String) = playlistRepository.getPlaylist(playistId)

    private fun getPrimaryPlaylistFlow(playlist: PrimaryPlaylist) =
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