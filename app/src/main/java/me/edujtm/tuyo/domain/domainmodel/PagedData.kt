package me.edujtm.tuyo.domain.domainmodel

data class PagedData<D, T>(
    val data: D,
    val prevPageToken: T,
    val nextPageToken: T
)
