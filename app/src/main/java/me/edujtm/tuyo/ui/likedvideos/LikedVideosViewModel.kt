package me.edujtm.tuyo.ui.likedvideos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.edujtm.tuyo.data.PlaylistItem
import me.edujtm.tuyo.repository.http.PlaylistHttpApi
import me.edujtm.tuyo.repository.http.RequestState
import me.edujtm.tuyo.repository.http.VideoHttpApi
import kotlin.coroutines.CoroutineContext

class LikedVideosViewModel(val playlistApi: PlaylistHttpApi) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val mutVideoInfo = MutableLiveData<RequestState<List<PlaylistItem>>>().apply {
        value = RequestState.Loading
    }
    val likedVideos : LiveData<RequestState<List<PlaylistItem>>> = mutVideoInfo

    fun getLikedVideos() {
        mutVideoInfo.value = RequestState.Loading
        launch {
            mutVideoInfo.value = playlistApi.getLikedVideos()
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}