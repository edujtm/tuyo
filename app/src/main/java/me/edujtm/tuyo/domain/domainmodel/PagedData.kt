package me.edujtm.tuyo.domain.domainmodel

typealias Token = String

data class PagedData<D>(
    val data: List<D>,
    val prevPageToken: Token?,
    val nextPageToken: Token?
)
