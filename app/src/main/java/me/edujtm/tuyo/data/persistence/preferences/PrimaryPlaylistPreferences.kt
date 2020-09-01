package me.edujtm.tuyo.data.persistence.preferences

import me.edujtm.tuyo.domain.domainmodel.PrimaryPlaylist
import me.edujtm.tuyo.domain.domainmodel.PrimaryPlaylistsIds

interface PrimaryPlaylistPreferences {
    suspend fun savePrimaryPlaylistIds(playlistIds: PrimaryPlaylistsIds)
    suspend fun getPrimaryPlaylistId(playlist: PrimaryPlaylist) : String?
}