package me.edujtm.tuyo.ui.playlistitems

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.*
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.data.model.PrimaryPlaylist
import me.edujtm.tuyo.domain.repository.PlaylistRepository
import me.edujtm.tuyo.domain.domainmodel.RequestState
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class PlaylistItemsViewModel
    @Inject constructor(
        val playlistRepository: PlaylistRepository<PagingData<PlaylistItem>>
    ) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    fun getPlaylist(playlistId: String) = playlistRepository.getPlaylist(playlistId)
        .cachedIn(this)

    fun getPrimaryPlaylist(playlist: PrimaryPlaylist)
            = playlistRepository.getPrimaryPlaylist(playlist).cachedIn(this)


    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}