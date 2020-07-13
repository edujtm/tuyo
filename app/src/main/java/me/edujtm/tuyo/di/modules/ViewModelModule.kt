package me.edujtm.tuyo.di.modules

import dagger.Module
import dagger.Provides
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.auth.Auth
import me.edujtm.tuyo.repository.http.YoutubePlaylistApi
import me.edujtm.tuyo.ui.home.HomeViewModel
import me.edujtm.tuyo.ui.likedvideos.LikedVideosViewModel
import me.edujtm.tuyo.ui.search.SearchViewModel


@Module
object ViewModelModule {

    @JvmStatic @Provides
    fun providesLikedVideoViewModel(youtubeApi: YoutubePlaylistApi) : LikedVideosViewModel
            = LikedVideosViewModel(youtubeApi)

    @JvmStatic @Provides
    fun providesSearchViewModel(): SearchViewModel = SearchViewModel()

    @JvmStatic @Provides
    fun providesHomeViewModel(): HomeViewModel = HomeViewModel()

    @JvmStatic @Provides
    fun providesMainViewModel(authManager: Auth): MainViewModel
            = MainViewModel(authManager)
}