package me.edujtm.tuyo

import androidx.lifecycle.ViewModel
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import javax.inject.Inject

class MainViewModel
    @Inject constructor() : ViewModel() {

    private val eventEmitter = EventEmitter<Event>()
    val events: EventSource<Event> = eventEmitter

    // I dislike this implementation, but it was the simplest way to handle the context
    // necessary for the GoogleApiAvailabity methods without spreading code in the fragments
    fun checkGoogleApiServices() {
        eventEmitter.emit(Event.CheckGooglePlayServices)
    }

    sealed class Event {
        object CheckGooglePlayServices : Event()
    }
}