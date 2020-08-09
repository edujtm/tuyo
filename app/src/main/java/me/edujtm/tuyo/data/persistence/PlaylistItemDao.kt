package me.edujtm.tuyo.data.persistence

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import me.edujtm.tuyo.data.model.PlaylistItem

@Dao
interface PlaylistItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(playlistItems: List<PlaylistItem>)

    @Query("SELECT * FROM playlist_items WHERE playlistId = :playlistId")
    fun getPlaylistItemsById(playlistId: String): Flow<List<PlaylistItem>>

    @Query("DELETE FROM  playlist_items WHERE playlistId = :playlistId")
    fun deletePlaylist(playlistId: String)

    @Delete
    suspend fun deletePlaylistItem(playlistItem: PlaylistItem)
}