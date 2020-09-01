package me.edujtm.tuyo.domain.domainmodel

/**
 * Represents the IDs of the user's main YouTube playlists.
 * This is retrieved by querying the Channels API endpoint.
 */
data class PrimaryPlaylistsIds(
    val likedVideos: String,
    val history: String,
    val favorites: String,
    val watchLater: String
) {
    fun selectPlaylist(primaryPlaylist: PrimaryPlaylist) : String {
        return when (primaryPlaylist) {
            PrimaryPlaylist.FAVORITES -> favorites
            PrimaryPlaylist.LIKED_VIDEOS -> likedVideos
            PrimaryPlaylist.WATCH_LATER -> watchLater
            PrimaryPlaylist.WATCH_HISTORY -> history
        }
    }
}

/**
 * Represents the intent to see one of the main YouTube playlists
 * when the respective ids aren't known yet.
 *
 * The main playlists are reachable from the navigation drawer,
 * this allows for the request for the playlist ID to be made after
 * the screen (fragment) was loaded.
 */
enum class PrimaryPlaylist {
    LIKED_VIDEOS,
    WATCH_HISTORY,
    FAVORITES,
    WATCH_LATER
}


/**
 * This class helps with some APIs on the UI layer, due to the limitation on
 * on which data [android.os.Bundle] can carry.
 *
 * The [me.edujtm.tuyo.ui.playlistitems.PlaylistFragment] is initialized with
 * either a playlist string ID or an [PrimaryPlaylist] enum for the main playlists
 * from YouTube (e.g liked videos, favorites).
 *
 * This caused duplication on some APIs and some if-elses to check which parameter
 * was passed in the bundle.
 */
sealed class SelectedPlaylist {
    data class Primary(val playlist: PrimaryPlaylist) : SelectedPlaylist()
    data class Extra(val playlistId: String): SelectedPlaylist()
}
