package me.edujtm.tuyo.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import me.edujtm.tuyo.auth.Auth
import me.edujtm.tuyo.auth.AuthManager
import me.edujtm.tuyo.auth.CredentialFactory
import me.edujtm.tuyo.repository.http.PlaylistHttpApi
import me.edujtm.tuyo.repository.http.YoutubePlaylistApi

@Module
object YoutubeApiModule {

    @JvmStatic @Provides
    fun providesYoutubeHttpApi(credentialFactory: CredentialFactory) : PlaylistHttpApi
        = YoutubePlaylistApi(credentialFactory)

    @JvmStatic @Provides
    fun providesCredentialFactory(context: Context, authManager: Auth)
            = CredentialFactory(context, authManager)

    @JvmStatic @Provides
    fun providesAuthManager(context: Context) : Auth = AuthManager(context)
}