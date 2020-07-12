package me.edujtm.tuyo.data

data class PlaylistItem(
    val id: String,
    val channelId: String,
    val title: String,
    val description: String,
    val playlistId: String,
    val videoId: String
) {
    companion object {
        // TODO: Maybe change this to a better abstraction
        fun fromJson(json: com.google.api.services.youtube.model.PlaylistItem) : PlaylistItem {
            val snippet = json.snippet
            return PlaylistItem(
                json.id,
                snippet.channelId,
                snippet.title,
                snippet.description,
                snippet.playlistId,
                json.contentDetails.videoId
            )
        }
    }
}
