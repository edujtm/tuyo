package me.edujtm.tuyo.data.endpoint

import me.edujtm.tuyo.data.model.PlaylistHeaderJson
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds
import me.edujtm.tuyo.domain.domainmodel.PagedData
import me.edujtm.tuyo.domain.domainmodel.Token

// TODO: Create a repository for the PrimaryPlaylistsIds
/** Retrieves information about the user from an API */
interface UserEndpoint {
    suspend fun getPrimaryPlaylistsIds() : PrimaryPlaylistsIds
    suspend fun getUserPlaylists(token: Token? = null): PagedData<PlaylistHeaderJson>
}