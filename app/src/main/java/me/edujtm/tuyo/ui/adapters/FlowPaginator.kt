package me.edujtm.tuyo.ui.adapters

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow


@ExperimentalCoroutinesApi
@FlowPreview
class FlowPaginator : EndlessRecyclerViewScrollListener {

    constructor(layoutManager: LinearLayoutManager) : super(layoutManager)
    constructor(layoutManager: GridLayoutManager) : super(layoutManager)
    constructor(layoutManager: StaggeredGridLayoutManager) : super(layoutManager)

    private val eventEmitter = ConflatedBroadcastChannel<Pair<Int, Int>>()
    val events : Flow<Pair<Int, Int>> = eventEmitter.asFlow()

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
        eventEmitter.offer(page to totalItemsCount)
    }
}