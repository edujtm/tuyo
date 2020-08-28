package me.edujtm.tuyo.ui.playlistitems

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import me.edujtm.tuyo.data.model.*
import me.edujtm.tuyo.domain.DispatcherProvider
import me.edujtm.tuyo.domain.domainmodel.Playlist
import me.edujtm.tuyo.domain.domainmodel.RequestState
import me.edujtm.tuyo.domain.repository.PlaylistRepository
import me.edujtm.tuyo.domain.repository.UserRepository
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class PlaylistViewModel
    @Inject constructor(
        val playlistRepository: PlaylistRepository,
        val userRepository: UserRepository,
        val dispatchers: DispatcherProvider
    ) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + dispatchers.main

    /** Works as a memory cache for the values from the DB */
    private val _playlistItems = MutableStateFlow<RequestState<Playlist>>(RequestState.Loading)
    val playlistItems: StateFlow<RequestState<Playlist>> = _playlistItems

    fun refresh(selectedPlaylist: SelectedPlaylist) {
        launch {
            val playlistId = when (selectedPlaylist) {
                is SelectedPlaylist.Primary -> {
                    userRepository.getPrimaryPlaylistId(selectedPlaylist.playlist)
                }
                is SelectedPlaylist.Extra -> selectedPlaylist.playlistId
            }
            playlistRepository.deletePlaylist(playlistId)
        }
    }

    fun requestPlaylistItems(selectedPlaylist: SelectedPlaylist, pageToken: String? = null) {
        launch {
            try {
                val playlistId = when (selectedPlaylist) {
                    is SelectedPlaylist.Primary -> {
                        userRepository.getPrimaryPlaylistId(selectedPlaylist.playlist)
                    }
                    is SelectedPlaylist.Extra -> selectedPlaylist.playlistId
                }

                playlistRepository.requestPlaylistItems(playlistId, pageToken)
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
        flow { emit(userRepository.getPrimaryPlaylistId(playlist)) }
            .flowOn(dispatchers.io)
            .flatMapConcat { playlistId ->
                playlistRepository.getPlaylist(playlistId)
            }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}