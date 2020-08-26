package me.edujtm.tuyo.common

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.AsyncTask
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

object GoogleApi {
    /** Used as the requestCode for the authentication process */
    const val REQUEST_AUTHORIZATION = 1001

    val api = GoogleApiAvailability.getInstance()

    fun isAvailable(context: Context) = getAvailabilityStatus(context) is StatusResult.Success

    fun getAvailabilityStatus(context: Context) : StatusResult {
        val resultCode = api.isGooglePlayServicesAvailable(context)
        return StatusResult.from(resultCode)
    }

    fun getErrorDialog(context: Activity, errorCode: Int, requestCode: Int) : Dialog {
        return api.getErrorDialog(context, errorCode, requestCode)
    }

    sealed class StatusResult {
        object Success: StatusResult() {
            override val resultCode: Int = ConnectionResult.SUCCESS
        }
        data class UserResolvableError(override val resultCode: Int) : StatusResult()
        data class NotResolvableError(override val resultCode: Int) : StatusResult()

        abstract val resultCode: Int

        companion object {
            fun from(resultCode: Int): StatusResult {
                return when {
                    resultCode == ConnectionResult.SUCCESS -> Success
                    api.isUserResolvableError(resultCode) -> UserResolvableError(resultCode)
                    else -> NotResolvableError(resultCode)
                }
            }
        }
    }
}