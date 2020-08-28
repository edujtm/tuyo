package me.edujtm.tuyo.di.components

import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import me.edujtm.tuyo.MainActivity
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.di.modules.YoutubeApiModule
import me.edujtm.tuyo.di.qualifier.UserEmail
import me.edujtm.tuyo.di.scopes.PerUserSession
import me.edujtm.tuyo.ui.home.HomeViewModel
import me.edujtm.tuyo.ui.playlistitems.PlaylistViewModel
import me.edujtm.tuyo.ui.search.SearchViewModel


@PerUserSession
@Subcomponent(modules = [
    YoutubeApiModule::class
])
interface MainActivityComponent {
    val searchViewModel: SearchViewModel
    val homeViewModel: HomeViewModel
    val playlistViewModel: PlaylistViewModel
    val mainViewModel: MainViewModel

    fun inject(activity: MainActivity)

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance
            @UserEmail
            userEmail: String
        ) : MainActivityComponent
    }

    @Module(subcomponents = [MainActivityComponent::class])
    interface InstallModule
}