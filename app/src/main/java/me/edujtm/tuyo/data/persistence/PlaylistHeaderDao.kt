package me.edujtm.tuyo.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.edujtm.tuyo.data.model.PlaylistHeaderDB

@Dao
interface PlaylistHeaderDao {

    @Insert
    suspend fun insertAll(playlistHeaders: List<PlaylistHeaderDB>)

    @Query("SELECT * FROM playlist_header WHERE ownerId = :userId")
    fun getUserPlaylists(userId: String): Flow<List<PlaylistHeaderDB>>
}