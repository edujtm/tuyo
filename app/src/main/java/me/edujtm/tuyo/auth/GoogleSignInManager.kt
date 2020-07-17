package me.edujtm.tuyo.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSignInManager
    @Inject constructor(context: Context) : AuthManager {


    private val appContext = context.applicationContext
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        GoogleSignIn.getClient(context, gso)
    }

    override fun getUserAccount(): GoogleAccount? {
        val account = getAccount()

        // id and email will not be null due to GoogleSignInOptions.DEFAULT_SIGN_IN
        // and requestEmail() being setup
        return account?.let {
            GoogleAccount(it.id!!, it.email!!, it.displayName!!, it.photoUrl?.toString())
        }
    }

    override fun getSignInIntent(): Intent = googleSignInClient.signInIntent

    override fun parseResultIntent(result: Intent?): AuthResult {
        return try {
            GoogleSignIn.getSignedInAccountFromIntent(result).getResult(ApiException::class.java)
            // Will not be null since user just signed in
            val account = getUserAccount()!!
            AuthResult.Success(account)
        } catch (e: Exception) {
            AuthResult.Failure(e)
        }
    }

    override fun signOut(callback: () -> Unit) {
        googleSignInClient.signOut()?.addOnCompleteListener {
            callback()
        }
    }

    fun getAccount() = GoogleSignIn.getLastSignedInAccount(appContext)
}