package me.edujtm.tuyo.data.model

typealias PlaylistId = String

data class PrimaryPlaylists(
    val likedVideos: PlaylistId,
    val history: PlaylistId,
    val favorites: PlaylistId,
    val watchLater: PlaylistId
) {

    // TODO: see if I'm able to turn this into an enum with R8 optimization
    companion object {
        const val LIKED_VIDEOS = 0
        const val WATCH_HISTORY = 1
        const val FAVORITES = 2
        const val WATCH_LATER = 3
    }
}
