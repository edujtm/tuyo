package me.edujtm.tuyo

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.data.persistence.PlaylistItemDao

/**
 * This is not optimzed in any way, but its only used in tests with minimal amount
 * of data.
 */
@ExperimentalCoroutinesApi
@FlowPreview
class InMemoryPlaylistItemDao : PlaylistItemDao {

    val items = mutableListOf<PlaylistItem>()

    /**
     * Emits all the playlist items on the database, not only the
     *  playlist that is being listened by a subscriber.
     */
    val dataEmitter = ConflatedBroadcastChannel(items)

    override fun deletePlaylist(playlistId: String) {
        items.removeIf { it.playlistId == playlistId }
        dataEmitter.offer(items)
    }

    override suspend fun insertAll(playlistItems: List<PlaylistItem>) {
        if (playlistItems.isNotEmpty()) {
            for (item in playlistItems) {
                items.removeIf { it.id == item.id }
            }
            items.addAll(playlistItems)
            dataEmitter.offer(items)
        }
    }

    override suspend fun deletePlaylistItem(playlistItem: PlaylistItem) {
        items.removeIf { it.id == playlistItem.id }
        dataEmitter.offer(items)
    }

    /**
     * Gets a Hot Flow that only emits the items of the playlist identified
     * by [playlistId].
     */
    override fun getPlaylistItemsById(playlistId: String): Flow<List<PlaylistItem>> {
        return dataEmitter.asFlow()
            .map {
                it.filter { playlistItem -> playlistItem.playlistId == playlistId}
            }
    }

    fun onStart() {
        dataEmitter.openSubscription()
    }

    fun onDestroy() {
        dataEmitter.close()
    }
}