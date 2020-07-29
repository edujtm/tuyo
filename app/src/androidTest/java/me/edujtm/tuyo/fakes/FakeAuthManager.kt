package me.edujtm.tuyo.fakes

import android.content.Intent
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager
import me.edujtm.tuyo.auth.AuthManager
import me.edujtm.tuyo.auth.AuthResult
import me.edujtm.tuyo.auth.GoogleAccount
import javax.inject.Inject

class FakeAuthManager
    @Inject constructor() : AuthManager {

    private var account: GoogleAccount? = GoogleAccount(
        id = "super-random-id",
        email = "example.user@gmail.com",
        displayName = "Eduardo Macedo",
        photoUrl = "https://placekitten.com/200/200"
    )

    override fun getSignInIntent(): Intent {
        return Intent().apply {
            action = "com.google.android.gms.common.account.CHOOSE_ACCOUNT"
            putExtra("allowableAccountTypes", arrayOf(GoogleAccountManager.ACCOUNT_TYPE))
        }
    }

    override fun getUserAccount(): GoogleAccount? {
        return account
    }

    override fun parseResultIntent(result: Intent?): AuthResult {
        return result?.let {
            account = GoogleAccount(
                id = "super-random-id",
                email = "example.user@gmail.com",
                displayName = "Eduardo Macedo",
                photoUrl = "https://placekitten.com/200/200"
            )
            AuthResult.Success(account!!)
        } ?: AuthResult.Failure(Throwable("Authentication error"))
    }

    override fun signOut(callback: () -> Unit) {
        account = null
        callback()
    }
}
