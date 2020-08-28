package me.edujtm.tuyo.fakes

import me.edujtm.tuyo.data.model.PrimaryPlaylist
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds
import me.edujtm.tuyo.data.persistence.preferences.PrimaryPlaylistPreferences
import javax.inject.Inject

class FakePrimaryPlaylistPreferences
    @Inject constructor() : PrimaryPlaylistPreferences {

    private var playlistIds: PrimaryPlaylistsIds? = null

    override suspend fun getPrimaryPlaylistId(playlist: PrimaryPlaylist): String? {
        return playlistIds?.selectPlaylist(playlist)
    }

    override suspend fun savePrimaryPlaylistIds(playlistIds: PrimaryPlaylistsIds) {
        this.playlistIds = playlistIds
    }
}