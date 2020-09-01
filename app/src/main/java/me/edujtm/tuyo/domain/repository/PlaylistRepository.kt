package me.edujtm.tuyo.domain.repository

import kotlinx.coroutines.flow.Flow
import me.edujtm.tuyo.domain.domainmodel.Playlist
import me.edujtm.tuyo.domain.domainmodel.Token

/** Retrieves Items from a single playlist */
interface PlaylistRepository {
    suspend fun requestPlaylistItems(playlistId: String, token: Token? = null)
    fun getPlaylist(playlistId: String): Flow<Playlist>

    /** Updates the content of the playlist in case it's outdated */
    suspend fun refresh(playlistId: String)
}