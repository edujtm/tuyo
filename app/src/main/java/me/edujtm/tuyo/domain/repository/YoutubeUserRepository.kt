package me.edujtm.tuyo.domain.repository

import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.model.PrimaryPlaylist
import javax.inject.Inject

class YoutubeUserRepository
    @Inject constructor(
        val userEndpoint: UserEndpoint
    ) : UserRepository {

    override suspend fun getPrimaryPlaylistId(playlist: PrimaryPlaylist): String {
        val playlistsIds = userEndpoint.getPrimaryPlaylistsIds()
        return playlistsIds.selectPlaylist(playlist)
    }
}