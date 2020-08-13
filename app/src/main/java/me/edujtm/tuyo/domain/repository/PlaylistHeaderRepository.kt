package me.edujtm.tuyo.domain.repository

import kotlinx.coroutines.flow.Flow
import me.edujtm.tuyo.domain.domainmodel.PlaylistHeader
import me.edujtm.tuyo.domain.domainmodel.Token

interface PlaylistHeaderRepository {
    fun getUserPlaylists(): Flow<List<PlaylistHeader>>
    suspend fun requestPlaylistHeaders(token: Token? = null)
}