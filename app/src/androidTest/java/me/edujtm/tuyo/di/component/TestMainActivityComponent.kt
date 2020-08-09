package me.edujtm.tuyo.di.component

import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import me.edujtm.tuyo.MainActivity
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.di.components.MainActivityComponent
import me.edujtm.tuyo.di.modules.FakeYoutubeModule
import me.edujtm.tuyo.di.qualifier.UserEmail
import me.edujtm.tuyo.ui.home.HomeViewModel
import me.edujtm.tuyo.ui.playlistitems.PlaylistItemsViewModel
import me.edujtm.tuyo.ui.search.SearchViewModel

@Subcomponent(modules = [
    FakeYoutubeModule::class
])
interface TestMainActivityComponent : MainActivityComponent {
    override val searchViewModel: SearchViewModel
    override val homeViewModel: HomeViewModel
    override val playlistItemsViewModel: PlaylistItemsViewModel
    override val mainViewModel: MainViewModel

    override fun inject(activity: MainActivity)

    @Subcomponent.Factory
    interface Factory : MainActivityComponent.Factory {
        override fun create(
            @BindsInstance @UserEmail userEmail: String
        ) : MainActivityComponent
    }

    @Module(subcomponents = [TestMainActivityComponent::class])
    interface InstallModule
}