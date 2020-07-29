package me.edujtm.tuyo.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import me.edujtm.tuyo.data.persistence.YoutubeDatabase

@Module
object FakePersistenceModule {

    @JvmStatic @Provides
    fun provideYoutubeDatabase(context: Context) : YoutubeDatabase {
        return Room.inMemoryDatabaseBuilder(context, YoutubeDatabase::class.java).build()
    }
}