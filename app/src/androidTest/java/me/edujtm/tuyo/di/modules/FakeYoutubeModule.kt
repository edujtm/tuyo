package me.edujtm.tuyo.di.modules

import dagger.Binds
import dagger.Module
import dagger.Provides
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.persistence.YoutubeDatabase
import me.edujtm.tuyo.data.persistence.preferences.PrimaryPlaylistPreferences
import me.edujtm.tuyo.domain.repository.*
import me.edujtm.tuyo.fakes.FakePlaylistEndpoint
import me.edujtm.tuyo.fakes.FakePrimaryPlaylistPreferences
import me.edujtm.tuyo.fakes.FakeUserEnpoint

@Module
abstract class FakeYoutubeModule {

    @Binds
    abstract fun provideUserEndpoint(fakeUserEndpoint: FakeUserEnpoint): UserEndpoint

    @Binds
    abstract fun providePlaylistEndpoint(fakePlaylistEndpoint: FakePlaylistEndpoint): PlaylistEndpoint

    @Binds
    abstract fun providePlaylistRepository(
        repository: YoutubePlaylistRepository
    ): PlaylistRepository

    @Binds
    abstract fun providePlaylistHeaderRepository(
        repository: YoutubePlaylistHeaderRepository
    ): PlaylistHeaderRepository

    @Binds
    abstract fun provideUserRepository(
        repository: YoutubeUserRepository
    ): UserRepository

    @Binds
    abstract fun providePrimaryPlaylistPreferences(
        pref: FakePrimaryPlaylistPreferences
    ): PrimaryPlaylistPreferences
}