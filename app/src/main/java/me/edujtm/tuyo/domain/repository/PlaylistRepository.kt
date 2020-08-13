package me.edujtm.tuyo.domain.repository

import kotlinx.coroutines.flow.Flow
import me.edujtm.tuyo.domain.domainmodel.PlaylistItem
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds
import me.edujtm.tuyo.domain.domainmodel.Token


interface PlaylistRepository {
    suspend fun getPrimaryPlaylistsIds(): PrimaryPlaylistsIds
    suspend fun requestPlaylistItems(playlistId: String, token: Token? = null)
    fun getPlaylist(playlistId: String): Flow<List<PlaylistItem>>
}