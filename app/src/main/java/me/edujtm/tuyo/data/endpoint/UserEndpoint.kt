package me.edujtm.tuyo.data.endpoint

import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds

/** Retrieves information about the user from an API */
interface UserEndpoint {
    fun getPrimaryPlaylistsIds() : PrimaryPlaylistsIds
}