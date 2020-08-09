package me.edujtm.tuyo.fakes

import me.edujtm.tuyo.Fake
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds
import javax.inject.Inject

class FakeUserEnpoint
    @Inject constructor() : UserEndpoint {

    private val playlistsIds by lazy {
        Fake.primaryPlaylistsIds().first()
    }
    override suspend fun getPrimaryPlaylistsIds(): PrimaryPlaylistsIds {
        return playlistsIds
    }
}