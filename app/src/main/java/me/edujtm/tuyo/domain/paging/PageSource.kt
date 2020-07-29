package me.edujtm.tuyo.domain.paging

import kotlinx.coroutines.flow.Flow

/**
 * This class only exists so I can test paging library 3 by
 * removing the dependency on PagingData objects.
 * It only allows me to test the Repository. ViewModel still sucks
 */
interface PageSource<K, T> {
    fun getPages(query: K): Flow<T>
}