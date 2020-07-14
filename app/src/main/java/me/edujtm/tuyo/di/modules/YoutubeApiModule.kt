package me.edujtm.tuyo.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import me.edujtm.tuyo.auth.AuthManager
import me.edujtm.tuyo.auth.CredentialFactory
import me.edujtm.tuyo.auth.GoogleSignInManager
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.endpoint.YoutubePlaylistEndpoint
import me.edujtm.tuyo.data.endpoint.YoutubeUserEndpoint
import me.edujtm.tuyo.domain.repository.PlaylistRepository
import me.edujtm.tuyo.domain.repository.YoutubePlaylistRepository

@Module
object YoutubeApiModule {

    @JvmStatic @Provides
    fun provideYoutubePlaylistRepository(
        playlistEndpoint: PlaylistEndpoint,
        userEndpoint: UserEndpoint
    ): PlaylistRepository = YoutubePlaylistRepository(playlistEndpoint, userEndpoint)

    @JvmStatic @Provides
    fun provideUserEndpoint(credentialFactory: CredentialFactory) : UserEndpoint
            = YoutubeUserEndpoint(credentialFactory)

    @JvmStatic @Provides
    fun providePlaylistEndpoint(credentialFactory: CredentialFactory): PlaylistEndpoint
            = YoutubePlaylistEndpoint(credentialFactory)

    @JvmStatic @Provides
    fun provideCredentialFactory(context: Context, authManager: AuthManager)
            = CredentialFactory(context, authManager)

    @JvmStatic @Provides
    fun provideAuthManager(context: Context) : AuthManager = GoogleSignInManager(context)
}