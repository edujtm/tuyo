package me.edujtm.tuyo.domain.repository

import kotlinx.coroutines.flow.Flow
import me.edujtm.tuyo.domain.domainmodel.PlaylistHeader

interface PlaylistHeaderRepository {
    fun getUserPlaylists(): Flow<List<PlaylistHeader>>
    suspend fun requestPlaylistHeaders(token: String? = null)
}