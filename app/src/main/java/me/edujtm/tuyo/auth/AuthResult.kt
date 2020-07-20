package me.edujtm.tuyo.auth

sealed  class AuthResult {
    data class Success(val account: GoogleAccount) : AuthResult()
    data class Failure(val error: Throwable): AuthResult()
}