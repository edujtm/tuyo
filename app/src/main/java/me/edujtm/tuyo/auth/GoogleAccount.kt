package me.edujtm.tuyo.auth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

/**
    Holds some information from the [GoogleSignInAccount]
    so that I can test the [me.edujtm.tuyo.MainViewModel] by mocking its [AuthResult] dependency
 */
data class GoogleAccount(
    val id: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?
)
