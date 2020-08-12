package me.edujtm.tuyo

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import me.edujtm.tuyo.data.model.PlaylistHeaderDB
import me.edujtm.tuyo.data.persistence.PlaylistHeaderDao

@ExperimentalCoroutinesApi
@FlowPreview
class InMemoryPlaylistHeaderDao : PlaylistHeaderDao {
    val items = mutableListOf<PlaylistHeaderDB>()
    val dataEmitter = ConflatedBroadcastChannel<List<PlaylistHeaderDB>>(items)

    override suspend fun insertAll(playlistHeaders: List<PlaylistHeaderDB>) {
        if (playlistHeaders.isNotEmpty()) {
            // Replace if already exists
            for (item in playlistHeaders) {
                items.removeIf { it.id == item.id }
            }
            items.addAll(playlistHeaders)
            dataEmitter.offer(items)
        }
    }


    override fun getUserPlaylists(userId: String): Flow<List<PlaylistHeaderDB>> {
        return dataEmitter.asFlow()
            .map { dbItems ->
                dbItems.filter { it.ownerId == userId }
            }
    }

    fun onStart() {
        dataEmitter.openSubscription()
    }

    fun onDestroy() {
        dataEmitter.close()
    }
}