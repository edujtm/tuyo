package me.edujtm.tuyo.domain.repository

import kotlinx.coroutines.flow.Flow
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.data.model.PrimaryPlaylist
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds


interface PlaylistRepository {
    suspend fun getPrimaryPlaylistsIds(): PrimaryPlaylistsIds
    suspend fun requestPlaylistItems(playlistId: String, token: String? = null)
    fun getPlaylist(playlistId: String): Flow<List<PlaylistItem>>
}