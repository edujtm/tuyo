package me.edujtm.tuyo.data.persistence

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import me.edujtm.tuyo.data.model.PlaylistItemDB

@Dao
interface PlaylistItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(playlistItems: List<PlaylistItemDB>)

    @Query("SELECT * FROM playlist_items WHERE playlistId = :playlistId")
    fun getPlaylistItemsById(playlistId: String): Flow<List<PlaylistItemDB>>

    @Query("DELETE FROM  playlist_items WHERE playlistId = :playlistId")
    fun deletePlaylist(playlistId: String)

    @Delete
    suspend fun deletePlaylistItem(playlistItem: PlaylistItemDB)
}