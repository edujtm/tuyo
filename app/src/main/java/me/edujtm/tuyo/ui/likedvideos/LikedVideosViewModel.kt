package me.edujtm.tuyo.ui.likedvideos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.edujtm.tuyo.repository.http.RequestState
import me.edujtm.tuyo.repository.http.VideoRepository
import kotlin.coroutines.CoroutineContext

class LikedVideosViewModel(val videoRepository: VideoRepository) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val mutVideoInfo = MutableLiveData<RequestState<List<String>>>().apply {
        value = RequestState.Success(listOf("This is Liked Videos Fragment"))
    }
    val videoInfo : LiveData<RequestState<List<String>>> = mutVideoInfo

    fun getVideoInfo() {
        mutVideoInfo.value = RequestState.Loading
        launch {
            mutVideoInfo.value = videoRepository.getVideoInfo()
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}