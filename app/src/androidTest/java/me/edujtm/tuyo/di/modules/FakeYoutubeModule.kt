package me.edujtm.tuyo.di.modules

import androidx.paging.PagingData
import dagger.Binds
import dagger.Module
import dagger.Provides
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.domain.repository.PlaylistRepository
import me.edujtm.tuyo.domain.repository.YoutubePlaylistRepository
import me.edujtm.tuyo.fakes.FakePageSource
import me.edujtm.tuyo.fakes.FakeUserEnpoint

@Module
abstract class FakeYoutubeModule {

    @Binds
    abstract fun provideUserEndpoint(fakeUserEndpoint: FakeUserEnpoint): UserEndpoint

    // The playlist endpoint is not used on tests (at least for now)

    companion object {
        @JvmStatic
        @Provides
        fun provideFakePlaylistRepository(
            pageSource: FakePageSource,
            userEndpoint: UserEndpoint
        ): PlaylistRepository<PagingData<PlaylistItem>> {
            return YoutubePlaylistRepository(userEndpoint, pageSource)
        }
    }
}