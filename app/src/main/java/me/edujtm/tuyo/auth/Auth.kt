package me.edujtm.tuyo.auth

import android.content.Intent

interface Auth {
    fun getUserAccount(): GoogleAccount?
    fun signOut(callback: () -> Unit)
    fun getSignInIntent(): Intent
}

