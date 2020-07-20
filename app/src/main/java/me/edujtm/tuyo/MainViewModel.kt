package me.edujtm.tuyo

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import me.edujtm.tuyo.auth.AuthManager
import me.edujtm.tuyo.auth.AuthState
import me.edujtm.tuyo.auth.GoogleAccount
import javax.inject.Inject

class MainViewModel
    @Inject constructor(val authManager: AuthManager) : ViewModel() {

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