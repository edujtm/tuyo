package me.edujtm.tuyo.unit

import io.mockk.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import me.edujtm.tuyo.CoroutineTestRule
import me.edujtm.tuyo.Fake
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.persistence.PlaylistItemDao
import me.edujtm.tuyo.domain.domainmodel.PagedData
import me.edujtm.tuyo.domain.repository.YoutubePlaylistRepository
import org.junit.Test

class YoutubePlaylistRepositoryTest {

    val testCoroutineRule = CoroutineTestRule(TestCoroutineDispatcher())

    val primaryPlaylists = Fake.primaryPlaylistsIds().first()

    val playlistEndpoint = mockk<PlaylistEndpoint>()
    val playlistItemDb = mockk<PlaylistItemDao>()
    val repo = YoutubePlaylistRepository(
        playlistEndpoint,
        playlistItemDb,
        testCoroutineRule.testDispatchers
    )

    @Test
    fun `request playlist item should put items into db`() =
        testCoroutineRule.testDispatcher.runBlockingTest {
        val playlistId = primaryPlaylists.likedVideos
        val items = Fake.Network.playlistItem(playlistId = playlistId).take(40).toList()
        val fakePage = PagedData(
            data = items,
            prevPageToken = null as String?,
            nextPageToken = null as String?
        )
        coEvery { playlistEndpoint.getPlaylistById(playlistId, any()) } returns fakePage
        coEvery { playlistItemDb.insertAll(any()) } just Runs

        // WHEN: requesting more items
        repo.requestPlaylistItems(playlistId)

        // THEN: the playlist endpoint should be called
        coVerify { playlistEndpoint.getPlaylistById(playlistId, any()) }
        coVerify { playlistItemDb.insertAll(any()) }
    }

}