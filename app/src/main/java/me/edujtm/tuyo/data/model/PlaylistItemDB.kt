package me.edujtm.tuyo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import me.edujtm.tuyo.domain.domainmodel.Token

/**
 * Represents an item inside a playlist when returned from the Database.
 * It's identical to [PlaylistItemJson] for now, but it'll have also
 * information about synchronization with the network in the future,
 * since updates to the playlist are made locally first.
 */
@Entity(tableName = "playlist_items")
data class PlaylistItemDB(
    @PrimaryKey
    val id: String,
    val channelId: String,
    val title: String,
    val description: String,
    val playlistId: String,
    val videoId: String,
    val thumbnailUrl: String?,
    val nextPageToken: Token?
)
