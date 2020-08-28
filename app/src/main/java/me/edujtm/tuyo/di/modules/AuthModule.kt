package me.edujtm.tuyo.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import me.edujtm.tuyo.auth.AuthManager
import me.edujtm.tuyo.auth.GoogleSignInManager
import me.edujtm.tuyo.di.qualifier.AppContext

@Module
object AuthModule {
    @JvmStatic @Provides
    fun provideAuthManager(@AppContext context: Context) : AuthManager = GoogleSignInManager(context)
}