package me.edujtm.tuyo.unit

import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import me.edujtm.tuyo.CoroutineTestRule
import me.edujtm.tuyo.Fake
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.persistence.PlaylistItemDao
import me.edujtm.tuyo.domain.domainmodel.PagedData
import me.edujtm.tuyo.domain.repository.YoutubePlaylistRepository
import org.junit.Test

@ExperimentalCoroutinesApi
@FlowPreview
class YoutubePlaylistRepositoryTest {

    val testCoroutineRule = CoroutineTestRule(TestCoroutineDispatcher())

    val primaryPlaylists = Fake.primaryPlaylistsIds().first()

    val playlistEndpoint = mockk<PlaylistEndpoint>()
    val userEndpoint = mockk<UserEndpoint>()
    val playlistItemDb = mockk<PlaylistItemDao>()
    val repo = YoutubePlaylistRepository(
        userEndpoint,
        playlistEndpoint,
        playlistItemDb,
        testCoroutineRule.testDispatchers
    )

    @Test
    fun test_request_playlist_item_should_put_items_into_db() = runBlockingTest {
        val playlistId = primaryPlaylists.likedVideos
        val items = Fake.playlistItem(id = playlistId).take(40).toList()
        val fakePage = PagedData(
            data = items,
            prevPageToken = null as String?,
            nextPageToken = null as String?
        )
        every { playlistEndpoint.getPlaylistById(playlistId, any()) } returns fakePage
        coEvery { playlistItemDb.insertAll(any()) } just Runs

        // WHEN: requesting more items
        repo.requestPlaylistItems(playlistId)

        // THEN: the playlist endpoint should be called
        verify { playlistEndpoint.getPlaylistById(playlistId, any()) }
        coVerify { playlistItemDb.insertAll(any()) }
    }

}