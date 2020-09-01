package me.edujtm.tuyo.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.edujtm.tuyo.domain.DispatcherProvider
import me.edujtm.tuyo.domain.domainmodel.PlaylistHeader
import me.edujtm.tuyo.domain.domainmodel.RequestState
import me.edujtm.tuyo.domain.repository.PlaylistHeaderRepository
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class HomeViewModel
    @Inject constructor(
        val repo: PlaylistHeaderRepository,
        val dispatchers: DispatcherProvider
    ) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + dispatchers.main

    private val _playlistHeaders = MutableStateFlow<RequestState<List<PlaylistHeader>>>(RequestState.Loading)
    val playlistHeaders : StateFlow<RequestState<List<PlaylistHeader>>> = _playlistHeaders

    /** Allows the UI to request for more pages on demand */
    fun requestPlaylistHeaders(token: String? = null) {
        launch {
            try {
                repo.requestPlaylistHeaders(token)
            } catch (e: Exception) {
                _playlistHeaders.value = RequestState.Failure(e)
            }
        }
    }

    suspend fun getUserPlaylists() {
        _playlistHeaders.value = RequestState.Loading
        repo.getUserPlaylists()
            .collect { headers ->
                if (headers.isEmpty()) {
                    requestPlaylistHeaders()
                }
                _playlistHeaders.value = RequestState.Success(headers)
            }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}