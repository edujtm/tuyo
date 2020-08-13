package me.edujtm.tuyo.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import me.edujtm.tuyo.data.model.PlaylistHeaderDB
import me.edujtm.tuyo.data.model.PlaylistItemDB
import javax.inject.Singleton


@Singleton
@Database(
    entities = [PlaylistItemDB::class, PlaylistHeaderDB::class],
    version = 1,
    exportSchema = false
)
abstract class YoutubeDatabase : RoomDatabase() {
    abstract fun playlistItemDao(): PlaylistItemDao
    abstract fun playlistHeaderDao(): PlaylistHeaderDao
}