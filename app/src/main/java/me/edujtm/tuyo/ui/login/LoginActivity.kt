package me.edujtm.tuyo.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.common.SignInButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import me.edujtm.tuyo.MainActivity
import me.edujtm.tuyo.R
import me.edujtm.tuyo.auth.AuthManager
import me.edujtm.tuyo.auth.AuthResult
import me.edujtm.tuyo.common.GoogleApi
import me.edujtm.tuyo.common.injector
import me.edujtm.tuyo.common.startActivity
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        injector.inject(this)

        supportActionBar?.hide()

        login_btn.setSize(SignInButton.SIZE_WIDE)
        login_btn.setOnClickListener {
            val signInIntent = authManager.getSignInIntent()
            startActivityForResult(signInIntent, REQUEST_LOGIN)
        }

        verifyUserLoggedIn()
        checkGooglePlayServices()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOGIN) {
            val result = authManager.parseResultIntent(data)
            when (result) {
                is AuthResult.Success -> {
                    startActivity<MainActivity> { intent ->
                        intent.putExtra(MainActivity.USER_EMAIL, result.account.email)
                    }
                    finish()
                }
                is AuthResult.Failure -> {
                    showMessage(result.error.message ?: "Unknown error")
                }
            }
        } else if (requestCode == REQUEST_GOOGLE_APIS) {
            // Check it again, there might be more than one error
            // If everything is correct, then nothing happens
            checkGooglePlayServices()
        }
    }

    private fun checkGooglePlayServices() {
        val status = GoogleApi.getAvailabilityStatus(this)
        when (status) {
            is GoogleApi.StatusResult.UserResolvableError -> showGoogleErrorDialog(status.resultCode)
            is GoogleApi.StatusResult.NotResolvableError -> showMessage("Google Api is necessary to use this app")
        }
    }

    private fun verifyUserLoggedIn() {
        val account = authManager.getUserAccount()
        if (account != null) {
            startActivity<MainActivity> { intent ->
                intent.putExtra(MainActivity.USER_EMAIL, account.email)
            }
            finish()
        }
    }

    private fun showGoogleErrorDialog(resultCode: Int) {
        val dialog = GoogleApi.getErrorDialog(this, resultCode, REQUEST_GOOGLE_APIS)
        dialog.show()
    }

    private fun showMessage(message: String) {
        Snackbar.make(login_activity_cl, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        const val REQUEST_LOGIN = 1001
        const val REQUEST_GOOGLE_APIS = 2001
    }
}