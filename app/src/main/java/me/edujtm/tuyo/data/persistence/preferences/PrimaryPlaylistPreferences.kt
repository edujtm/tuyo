package me.edujtm.tuyo.data.persistence.preferences

import me.edujtm.tuyo.data.model.PrimaryPlaylist
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds

interface PrimaryPlaylistPreferences {
    suspend fun savePrimaryPlaylistIds(playlistIds: PrimaryPlaylistsIds)
    suspend fun getPrimaryPlaylistId(playlist: PrimaryPlaylist) : String?
}