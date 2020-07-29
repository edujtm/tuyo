package me.edujtm.tuyo.fakes

import androidx.paging.PagingSource
import me.edujtm.tuyo.data.model.PlaylistItem


/** Mimics the remote mediator source for testing */
class FakePagingSource : PagingSource<Int, PlaylistItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PlaylistItem> {
        val page = params.key ?: 1
        val playlistItems = Fake.playlistItem().take(40).toList()

        val nextPage = if (page < 10) page + 1 else null

        return LoadResult.Page(
            data = playlistItems,
            prevKey = null,
            nextKey = nextPage
        )
    }
}