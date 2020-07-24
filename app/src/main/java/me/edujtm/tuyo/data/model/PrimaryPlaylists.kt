package me.edujtm.tuyo.data.model

typealias PlaylistId = String

data class PrimaryPlaylistsIds(
    val likedVideos: PlaylistId,
    val history: PlaylistId,
    val favorites: PlaylistId,
    val watchLater: PlaylistId
)

enum class PrimaryPlaylist {
    LIKED_VIDEOS,
    WATCH_HISTORY,
    FAVORITES,
    WATCH_LATER
}
