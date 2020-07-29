package me.edujtm.tuyo.di.component

import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import me.edujtm.tuyo.di.qualifier.UserEmail

@Subcomponent
interface TestMainActivityComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance @UserEmail userEmail: String
        ) : TestMainActivityComponent
    }

    @Module(subcomponents = [TestMainActivityComponent::class])
    interface InstallModule
}