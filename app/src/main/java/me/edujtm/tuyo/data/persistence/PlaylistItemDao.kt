package me.edujtm.tuyo.data.persistence

import androidx.paging.PagingSource
import androidx.room.*
import me.edujtm.tuyo.data.model.PlaylistItem

@Dao
interface PlaylistItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(playlistItems: List<PlaylistItem>)

    @Query("SELECT * FROM playlist_items WHERE id = :itemId")
    fun playlistItemsById(itemId: String): PagingSource<String, PlaylistItem>

    @Delete
    suspend fun deletePlaylistItem(playlistItem: PlaylistItem)
}