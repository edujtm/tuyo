package me.edujtm.tuyo.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import me.edujtm.tuyo.data.persistence.PlaylistHeaderDao
import me.edujtm.tuyo.data.persistence.PlaylistItemDao
import me.edujtm.tuyo.data.persistence.YoutubeDatabase
import me.edujtm.tuyo.di.qualifier.AppContext
import javax.inject.Singleton

@Module
object FakePersistenceModule {

    @JvmStatic
    @Provides @Singleton
    fun provideYoutubeDatabase(@AppContext context: Context) : YoutubeDatabase {
        return Room.inMemoryDatabaseBuilder(context, YoutubeDatabase::class.java).build()
    }

    @JvmStatic
    @Provides @Singleton
    fun providePlaylistItemDao(database: YoutubeDatabase) : PlaylistItemDao {
        return database.playlistItemDao()
    }

    @JvmStatic
    @Provides @Singleton
    fun providePlaylistHeaderDao(database: YoutubeDatabase): PlaylistHeaderDao {
        return database.playlistHeaderDao()
    }
}