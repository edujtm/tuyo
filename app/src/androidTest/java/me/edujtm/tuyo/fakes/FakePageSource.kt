package me.edujtm.tuyo.fakes

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.domain.paging.PageSource

class FakePageSource : PageSource<String, PagingData<PlaylistItem>> {

    override fun getPages(query: String): Flow<PagingData<PlaylistItem>> {
        return Pager(
            PagingConfig(pageSize = 40)
        ) {
            // Stops at 10 pages
            FakePagingSource()
        }.flow
    }
}