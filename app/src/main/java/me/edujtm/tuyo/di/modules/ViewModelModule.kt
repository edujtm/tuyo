package me.edujtm.tuyo.di.modules

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import dagger.Module
import dagger.Provides
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.auth.AuthManager
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.domain.repository.YoutubePlaylistRepository
import me.edujtm.tuyo.ui.home.HomeViewModel
import me.edujtm.tuyo.ui.likedvideos.LikedVideosViewModel
import me.edujtm.tuyo.ui.search.SearchViewModel


@Module
object ViewModelModule {

    @ExperimentalPagingApi
    @JvmStatic @Provides
    fun provideLikedVideoViewModel(youtubeApi: YoutubePlaylistRepository<PagingData<PlaylistItem>>) : LikedVideosViewModel
            = LikedVideosViewModel(youtubeApi)

    @JvmStatic @Provides
    fun provideSearchViewModel(): SearchViewModel = SearchViewModel()

    /*
    @JvmStatic @Provides
    fun provideMainViewModel(authManager: AuthManager): MainViewModel
            = MainViewModel(authManager)
     */
}