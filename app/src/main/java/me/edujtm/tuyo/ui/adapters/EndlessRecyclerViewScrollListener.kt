package me.edujtm.tuyo.ui.adapters

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class EndlessRecyclerViewScrollListener : RecyclerView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private var visibleThreshold = 10
    // The current offset index of data you have loaded
    private var currentPage = 0
    // The total number of items in the dataset after the last load
    private var previousTotalItemCount = 0
    // True if we are still waiting for the last set of data to load.
    private var loading = true
    // Sets the starting page index
    private var startingPageIndex = 0

    val layoutManager: RecyclerView.LayoutManager

    constructor(layoutManager: LinearLayoutManager) {
        this.layoutManager = layoutManager
    }

    constructor(layoutManager: GridLayoutManager) {
        this.layoutManager = layoutManager
        visibleThreshold *= layoutManager.spanCount
    }

    constructor(layoutManager: StaggeredGridLayoutManager) {
        this.layoutManager = layoutManager
        visibleThreshold *= layoutManager.spanCount
    }

    fun getLastVisibleItem(lastVisibleItemPositions: IntArray) : Int
            = lastVisibleItemPositions.max() ?: 0

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    override fun onScrolled(view:  RecyclerView, dx: Int, dy: Int) {
        val totalItemCount = layoutManager.itemCount

        val lastVisibleItemPosition = when (layoutManager) {
            is StaggeredGridLayoutManager -> getLastVisibleItem(layoutManager.findLastVisibleItemPositions(null))
            is GridLayoutManager -> layoutManager.findLastVisibleItemPosition()
            is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
            // This will not happen since layoutManager cant be initialized with wrong manager
            else -> throw IllegalStateException("RecyclerView.LayoutManager initialized with non supported" +
                    "manager")
        }

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }
        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
            currentPage++
            onLoadMore(currentPage, totalItemCount, view)
            loading = true
        }
    }

    // Call this method whenever performing new searches
    fun resetState() {
        currentPage = startingPageIndex
        previousTotalItemCount = 0
        loading = true
    }

    // Defines the process for actually loading more data based on page
    abstract fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView)

}