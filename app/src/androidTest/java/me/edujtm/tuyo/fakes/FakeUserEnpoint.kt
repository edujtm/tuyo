package me.edujtm.tuyo.fakes

import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds

class FakeUserEnpoint : UserEndpoint {
    override fun getPrimaryPlaylistsIds(): PrimaryPlaylistsIds {
        return Fake.primaryPlaylistsIds().first()
    }
}