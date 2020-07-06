package me.edujtm.tuyo.auth

sealed class AuthState<out T> {
    object Unauthenticated : AuthState<Nothing>()
    data class Authenticated<T>(val account: T) : AuthState<T>()
    data class InvalidAuthentication(val error: Throwable) : AuthState<Nothing>() {
        companion object {
            fun build(message: String) : InvalidAuthentication {
                val throwable = Throwable(message)
                return InvalidAuthentication(throwable)
            }
        }
    }
}