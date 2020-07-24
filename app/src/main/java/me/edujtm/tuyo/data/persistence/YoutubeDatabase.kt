package me.edujtm.tuyo.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.data.model.RemoteKeys
import javax.inject.Singleton


@Singleton
@Database(
    entities = [PlaylistItem::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class YoutubeDatabase : RoomDatabase() {
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun playlistItemDao(): PlaylistItemDao
}