package me.edujtm.tuyo.domain.repository

import me.edujtm.tuyo.domain.domainmodel.PrimaryPlaylist

interface UserRepository {
    /** Retrieves the String ID from one of the primary playlists of the current user. */
    suspend fun getPrimaryPlaylistId(playlist: PrimaryPlaylist): String
}