package me.edujtm.tuyo.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager
    @Inject constructor(context: Context) : Auth {


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

    override fun signOut(callback: () -> Unit) {
        googleSignInClient.signOut()?.addOnCompleteListener {
            callback()
        }
    }

    fun getAccount() = GoogleSignIn.getLastSignedInAccount(appContext)
}