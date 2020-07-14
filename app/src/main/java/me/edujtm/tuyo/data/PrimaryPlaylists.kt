package me.edujtm.tuyo.data

typealias PlaylistId = String

data class PrimaryPlaylists(
    val likedVideos: PlaylistId,
    val history: PlaylistId,
    val favorites: PlaylistId,
    val watchLater: PlaylistId
)
