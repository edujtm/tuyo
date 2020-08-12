package me.edujtm.tuyo.di.modules

import dagger.Binds
import dagger.Module
import dagger.Provides
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.persistence.YoutubeDatabase
import me.edujtm.tuyo.domain.repository.PlaylistHeaderRepository
import me.edujtm.tuyo.domain.repository.PlaylistRepository
import me.edujtm.tuyo.domain.repository.YoutubePlaylistHeaderRepository
import me.edujtm.tuyo.domain.repository.YoutubePlaylistRepository
import me.edujtm.tuyo.fakes.FakePlaylistEndpoint
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
}