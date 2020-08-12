package me.edujtm.tuyo.domain.domainmodel

data class PlaylistHeader(
    val id: String,
    val title: String,
    val thumbnailUrl: String,
    val publishedAt: String,
    val itemCount: Long,
    val nextPageToken: String?
)

