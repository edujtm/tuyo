package me.edujtm.tuyo.data.persistence.preferences

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import me.edujtm.tuyo.data.model.PrimaryPlaylist
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds
import me.edujtm.tuyo.di.qualifier.AppContext
import me.edujtm.tuyo.di.scopes.PerUserSession
import javax.inject.Inject

@PerUserSession
class UserPrimaryPlaylistPreferences
    @Inject constructor(@AppContext context: Context): PrimaryPlaylistPreferences {

    val preferences = context.getSharedPreferences(PLAYLIST_ID_CACHE, Context.MODE_PRIVATE)

    init {
        preferences.edit().clear().apply()
    }

    override suspend fun savePrimaryPlaylistIds(playlistIds: PrimaryPlaylistsIds) {
        preferences.edit {
            for (playlist in PrimaryPlaylist.values()) {
                val id = playlistIds.selectPlaylist(playlist)
                putString(playlist.name, id)
            }
        }
    }

    override suspend fun getPrimaryPlaylistId(playlist: PrimaryPlaylist): String? {
        return preferences.getString(playlist.name, null)
    }

    companion object {
        const val PLAYLIST_ID_CACHE = "PlaylistIds"
    }
}