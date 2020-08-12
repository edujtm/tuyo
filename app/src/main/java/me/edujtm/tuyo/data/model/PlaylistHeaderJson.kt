package me.edujtm.tuyo.data.model

import com.google.api.services.youtube.model.Playlist

/**
 * I Did this mapping so I'm able to mock out the API endpoints for tests since the
 * google API does not offer fake implementation or public constructors of the classes
 * returned.
 */
data class PlaylistHeaderJson(
    val id: String,
    val title: String,
    val itemCount: Long,
    val publishedAt: String,
    val thumbnail: String,
    val nextPageToken: String?
) {
    companion object {
        fun fromYoutubeModel(headerJson: Playlist, token: String? = null) : PlaylistHeaderJson {
            return PlaylistHeaderJson(
                id = headerJson.id,
                title = headerJson.snippet.title,
                itemCount = headerJson.contentDetails.itemCount,
                publishedAt = headerJson.snippet.publishedAt.toString(),
                thumbnail = headerJson.snippet.thumbnails.default.url,
                nextPageToken = token
            )
        }
    }
}