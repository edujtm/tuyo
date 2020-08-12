package me.edujtm.tuyo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "playlist_header")
data class PlaylistHeaderDB(
    @PrimaryKey
    val id: String,
    val ownerId: String,
    val title: String,
    val itemCount: Long,
    val thumbnailUrl: String,
    val publishedAt: String,
    val nextPageToken: String?
)