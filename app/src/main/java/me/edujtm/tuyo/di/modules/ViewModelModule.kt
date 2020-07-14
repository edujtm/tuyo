package me.edujtm.tuyo.di.modules

import dagger.Module
import dagger.Provides
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.auth.AuthManager
import me.edujtm.tuyo.domain.repository.YoutubePlaylistRepository
import me.edujtm.tuyo.ui.home.HomeViewModel
import me.edujtm.tuyo.ui.likedvideos.LikedVideosViewModel
import me.edujtm.tuyo.ui.search.SearchViewModel


@Module
object ViewModelModule {

    @JvmStatic @Provides
    fun provideLikedVideoViewModel(youtubeApi: YoutubePlaylistRepository) : LikedVideosViewModel
            = LikedVideosViewModel(youtubeApi)

    @JvmStatic @Provides
    fun provideSearchViewModel(): SearchViewModel = SearchViewModel()

    @JvmStatic @Provides
    fun provideHomeViewModel(): HomeViewModel = HomeViewModel()

    @JvmStatic @Provides
    fun provideMainViewModel(authManager: AuthManager): MainViewModel
            = MainViewModel(authManager)
}