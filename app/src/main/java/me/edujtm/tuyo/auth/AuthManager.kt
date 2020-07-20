package me.edujtm.tuyo.auth

import android.content.Intent

interface AuthManager {
    fun getUserAccount(): GoogleAccount?
    fun signOut(callback: () -> Unit)
    fun getSignInIntent(): Intent
    fun parseResultIntent(result: Intent?): AuthResult
}

