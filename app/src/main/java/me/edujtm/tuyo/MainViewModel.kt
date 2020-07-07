package me.edujtm.tuyo

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import me.edujtm.tuyo.auth.Auth
import me.edujtm.tuyo.auth.AuthState
import me.edujtm.tuyo.auth.GoogleAccount

class MainViewModel(val authManager: Auth) : ViewModel() {

    private val mutAuthState = MutableLiveData<AuthState<GoogleAccount>>(AuthState.Unauthenticated)
    val authState : LiveData<AuthState<GoogleAccount>> = mutAuthState

    private val mutGooglePlayState = MutableLiveData<Int>()
    private val googlePlayState : LiveData<Int> = mutGooglePlayState

    private val eventEmitter = EventEmitter<Event>()
    val events: EventSource<Event> = eventEmitter

    init {
        val currentAccount = authManager.getUserAccount()
        if (currentAccount != null) {
            mutAuthState.value = AuthState.Authenticated(currentAccount)
        } else {
            mutAuthState.value = AuthState.Unauthenticated
        }
    }

    fun signIn() {
        val signInEvent = Event.SignIn(authManager.getSignInIntent())
        eventEmitter.emit(signInEvent)
    }

    fun signOut() {
        authManager.signOut {
            mutAuthState.value = AuthState.Unauthenticated
        }
    }

    fun authenticate(signInResult: Intent?) {
        try {
           GoogleSignIn.getSignedInAccountFromIntent(signInResult).getResult(ApiException::class.java)
            val account = authManager.getUserAccount()
            // Will never be null since the user just logged in
            mutAuthState.value = AuthState.Authenticated(account!!)
            eventEmitter.emit(Event.SignInSuccess(account))
        } catch (e: ApiException) {
            mutAuthState.value = AuthState.InvalidAuthentication(e)
        }
    }

    fun denyAuthentication() {
        mutAuthState.value = AuthState.Unauthenticated
    }

    // I dislike this implementation, but it was the simplest way to handle the context
    // necessary for the GoogleApiAvailabity methods without spreading code in the fragments
    fun checkGoogleApiServices() {
        eventEmitter.emit(Event.CheckGooglePlayServices)
    }

    fun setGoogleApiResult(resultCode: Int) {
        val resultEvent = Event.GoogleApiServicesResult(resultCode)
        eventEmitter.emit(resultEvent)
    }

    sealed class Event {
        data class SignIn(val signInIntent: Intent) : Event()
        data class SignInSuccess(val account: GoogleAccount) : Event()
        object CheckGooglePlayServices : Event()
        data class GoogleApiServicesResult(val resultCode: Int) : Event()
    }
}