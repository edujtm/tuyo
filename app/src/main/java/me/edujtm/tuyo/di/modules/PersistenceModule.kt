package me.edujtm.tuyo.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import me.edujtm.tuyo.data.persistence.YoutubeDatabase

@Module
object PersistenceModule {

    @JvmStatic @Provides
    fun providesYoutubeDatabase(context: Context): YoutubeDatabase {
        return Room.databaseBuilder(
            context,
            YoutubeDatabase::class.java,
            "Youtube.db")
            .build()
    }
}