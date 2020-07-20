package me.edujtm.tuyo.data.model

typealias PlaylistId = String

data class PrimaryPlaylists(
    val likedVideos: PlaylistId,
    val history: PlaylistId,
    val favorites: PlaylistId,
    val watchLater: PlaylistId
)
