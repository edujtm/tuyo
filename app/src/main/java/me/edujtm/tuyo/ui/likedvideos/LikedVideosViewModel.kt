package me.edujtm.tuyo.ui.likedvideos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.domain.repository.PlaylistRepository
import me.edujtm.tuyo.domain.domainmodel.RequestState
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class LikedVideosViewModel
    @Inject constructor(val playlistApi: PlaylistRepository) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val mutVideoInfo = MutableLiveData<RequestState<List<PlaylistItem>>>().apply {
        value = RequestState.Loading
    }
    val likedVideos : LiveData<RequestState<List<PlaylistItem>>> = mutVideoInfo

    fun getLikedVideos() = playlistApi.getLikedVideos()
        .cachedIn(this)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}