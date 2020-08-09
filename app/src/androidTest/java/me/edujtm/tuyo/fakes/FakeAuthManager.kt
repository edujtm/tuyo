package me.edujtm.tuyo.fakes

import android.content.Intent
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager
import me.edujtm.tuyo.auth.AuthManager
import me.edujtm.tuyo.auth.AuthResult
import me.edujtm.tuyo.auth.GoogleAccount
import javax.inject.Inject

class FakeAuthManager
    @Inject constructor() : AuthManager {

    // Starts with a logged out user
    private var account: GoogleAccount? = null

    override fun getSignInIntent(): Intent {
        return Intent().apply {
            action = "com.google.android.gms.common.account.CHOOSE_ACCOUNT"
            putExtra("allowableAccountTypes", arrayOf(GoogleAccountManager.ACCOUNT_TYPE))
        }
    }

    override fun getUserAccount(): GoogleAccount? {
        return if (autoLogin) FAKE_ACCOUNT else account
    }

    override fun parseResultIntent(result: Intent?): AuthResult {
        return result?.let {
            account = FAKE_ACCOUNT
            AuthResult.Success(account!!)
        } ?: AuthResult.Failure(Throwable("Authentication error"))
    }

    override fun signOut(callback: () -> Unit) {
        account = null
        callback()
    }

    companion object {
        val FAKE_ACCOUNT = GoogleAccount(
            id = "super-random-id",
            email = "example.user@gmail.com",
            displayName = "Eduardo Macedo",
            photoUrl = "https://placekitten.com/200/200"
        )

        // Allows tests to override normal authentication
        // This was causing problems due to automatic redirection of logged out
        // users to login activity
        var autoLogin = false
    }
}
