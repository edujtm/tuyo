package me.edujtm.tuyo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.*
import javax.inject.Inject

class MainViewModel
    @Inject constructor() : ViewModel() {

    private val eventEmitter = Channel<Event>(Channel.Factory.UNLIMITED)
    val events: ReceiveChannel<Event> = eventEmitter

    fun sendEvent(event: Event) {
        eventEmitter.offer(event)
    }

    override fun onCleared() {
        eventEmitter.cancel()
    }

    sealed class Event {
        data class UiError(val error: Throwable) : Event()
    }
}