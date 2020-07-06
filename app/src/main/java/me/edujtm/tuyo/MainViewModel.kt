package me.edujtm.tuyo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.edujtm.tuyo.auth.AuthState

class MainViewModel : ViewModel() {

    private val mutAuthState = MutableLiveData<AuthState<String>>(AuthState.Unauthenticated)

    val authState : LiveData<AuthState<String>> = mutAuthState

    fun authenticate(name: String, password: String) {
        if (checkNameAndPassword(name, password)) {
            mutAuthState.value = AuthState.Authenticated(name)
        } else {
            mutAuthState.value = AuthState.InvalidAuthentication.build("Invalid name or password")
        }
    }

    fun denyAuthentication() {
        mutAuthState.value = AuthState.Unauthenticated
    }


    private fun checkNameAndPassword(name: String, password: String) : Boolean {
        return name == "Eduardo" && password == "password"
    }
}