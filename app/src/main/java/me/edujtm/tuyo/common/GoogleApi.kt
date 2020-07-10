package me.edujtm.tuyo.common

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

object GoogleApi {
    val api = GoogleApiAvailability.getInstance()

    fun isAvailable(context: Context) = getResultCode(context) == ConnectionResult.SUCCESS

    fun getResultCode(context: Context) : Int = api.isGooglePlayServicesAvailable(context)

    fun checkResultCode(resultCode: Int): AcquireResult {
        return when {
            resultCode == ConnectionResult.SUCCESS -> {
                AcquireResult.Success
            }
            api.isUserResolvableError(resultCode) -> {
                AcquireResult.UserResolvableError(resultCode)
            }
            else -> {
                AcquireResult.NotResolvableError(resultCode)
            }
        }
    }

    sealed class AcquireResult {
        object Success: AcquireResult()
        data class UserResolvableError(val resultCode: Int) : AcquireResult()
        data class NotResolvableError(val resultCode: Int) : AcquireResult()
    }
}