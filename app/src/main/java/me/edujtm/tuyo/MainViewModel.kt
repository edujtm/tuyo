package me.edujtm.tuyo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.*
import javax.inject.Inject

class MainViewModel
    @Inject constructor() : ViewModel() {

    private val eventEmitter = Channel<Event>(Channel.Factory.UNLIMITED)
    val events: ReceiveChannel<Event> = eventEmitter

    // I dislike this implementation, but it was the simplest way to handle the context
    // necessary for the GoogleApiAvailabity methods without spreading code in the fragments
    fun checkGoogleApiServices() {
        eventEmitter.offer(Event.CheckGooglePlayServices)
    }

    override fun onCleared() {
        eventEmitter.cancel()
    }

    sealed class Event {
        object CheckGooglePlayServices : Event()
    }
}