package me.edujtm.tuyo.data.model

import com.google.api.services.youtube.model.PlaylistItem
import me.edujtm.tuyo.domain.domainmodel.Token

/**
 *  Represents an item in a playlist when returned by the network
 *  This protects me against API changes which would cause database migrations,
 *  if I used the same model for both.
 *
 *  This class might be removed if I manage to understand how to generate fake [PlaylistItem]
 *  for testing.
 */
data class PlaylistItemJson(
    val id: String,
    val channelId: String,
    val title: String,
    val description: String,
    val playlistId: String,
    val videoId: String,
    val thumbnailUrl: String?,
    val nextPageToken: Token?
) {
    companion object {
        fun fromYoutubeModel(playlistItem: PlaylistItem, nextPageToken: Token?) : PlaylistItemJson {
            return PlaylistItemJson(
                id = playlistItem.id,
                channelId = playlistItem.snippet.channelId,
                title = playlistItem.snippet.title,
                description = playlistItem.snippet.description,
                playlistId = playlistItem.snippet.playlistId,
                videoId = playlistItem.contentDetails.videoId,
                thumbnailUrl = playlistItem.snippet.thumbnails.default.url,
                nextPageToken = nextPageToken
            )
        }
    }
}