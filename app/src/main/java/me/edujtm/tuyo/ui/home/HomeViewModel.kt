package me.edujtm.tuyo.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.edujtm.tuyo.domain.DispatcherProvider
import me.edujtm.tuyo.domain.domainmodel.PlaylistHeader
import me.edujtm.tuyo.domain.repository.PlaylistHeaderRepository
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class HomeViewModel
    @Inject constructor(
        val repo: PlaylistHeaderRepository,
        val dispatchers: DispatcherProvider
    ) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + dispatchers.main

    private val _playlistHeaders = MutableStateFlow<List<PlaylistHeader>>(emptyList())
    val playlistHeaders : StateFlow<List<PlaylistHeader>>
        get() = _playlistHeaders

    fun requestPlaylistHeaders(token: String? = null) {
        launch {
            repo.requestPlaylistHeaders(token)
        }
    }

    suspend fun getUserPlaylists() {
        repo.getUserPlaylists()
            .collect { headers ->
                if (headers.isEmpty()) {
                    repo.requestPlaylistHeaders()
                }
                _playlistHeaders.value = headers
            }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}