package me.edujtm.tuyo.ui.likedvideos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.cachedIn
import kotlinx.coroutines.*
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.data.model.PrimaryPlaylists
import me.edujtm.tuyo.domain.repository.PlaylistRepository
import me.edujtm.tuyo.domain.domainmodel.RequestState
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class LikedVideosViewModel
    @Inject constructor(
        val playlistRepository: PlaylistRepository
    ) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val mutVideoInfo = MutableLiveData<RequestState<List<PlaylistItem>>>().apply {
        value = RequestState.Loading
    }
    val likedVideos : LiveData<RequestState<List<PlaylistItem>>> = mutVideoInfo

    @ExperimentalCoroutinesApi
    fun getLikedVideos() = playlistRepository.getPrimaryPlaylist(PrimaryPlaylists.LIKED_VIDEOS)
        .cachedIn(this)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}