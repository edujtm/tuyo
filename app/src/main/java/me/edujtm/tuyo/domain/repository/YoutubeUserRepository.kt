package me.edujtm.tuyo.domain.repository

import kotlinx.coroutines.withContext
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.model.PrimaryPlaylist
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds
import me.edujtm.tuyo.data.persistence.preferences.PrimaryPlaylistPreferences
import me.edujtm.tuyo.domain.DispatcherProvider
import javax.inject.Inject

class YoutubeUserRepository
    @Inject constructor(
        val userEndpoint: UserEndpoint,
        val userPreferences: PrimaryPlaylistPreferences,
        val dispatchers: DispatcherProvider
    ) : UserRepository {

    override suspend fun getPrimaryPlaylistId(playlist: PrimaryPlaylist): String =
        withContext(dispatchers.io) {
            return@withContext userPreferences.getPrimaryPlaylistId(playlist)
                ?: userEndpoint.getPrimaryPlaylistsIds().also { playlistIds ->
                    userPreferences.savePrimaryPlaylistIds(playlistIds)
                }.selectPlaylist(playlist)
        }
}