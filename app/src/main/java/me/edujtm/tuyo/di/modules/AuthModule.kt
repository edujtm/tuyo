package me.edujtm.tuyo.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import me.edujtm.tuyo.auth.AuthManager
import me.edujtm.tuyo.auth.CredentialFactory
import me.edujtm.tuyo.auth.GoogleSignInManager

@Module
object AuthModule {
    @JvmStatic @Provides
    fun provideCredentialFactory(context: Context, authManager: AuthManager)
            = CredentialFactory(context, authManager)

    @JvmStatic @Provides
    fun provideAuthManager(context: Context) : AuthManager = GoogleSignInManager(context)
}