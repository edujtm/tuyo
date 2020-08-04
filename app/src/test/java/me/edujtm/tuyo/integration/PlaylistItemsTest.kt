package me.edujtm.tuyo.integration

import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import me.edujtm.tuyo.CoroutineTestRule
import me.edujtm.tuyo.Fake
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.model.SelectedPlaylist
import me.edujtm.tuyo.data.persistence.PlaylistItemDao
import me.edujtm.tuyo.domain.repository.YoutubePlaylistRepository
import me.edujtm.tuyo.ui.playlistitems.PlaylistItemsViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * These tests are for the whole Playlist Item infrastructure which includes ViewModel,
 * Repository and data layer. They test if the assumptions I have from the UI layer are
 * replicated on the data access layer.
 */
@ExperimentalCoroutinesApi
@FlowPreview
class PlaylistItemsTest {

    val testCoroutineRule = CoroutineTestRule(TestCoroutineDispatcher())

    val playlistsIds = Fake.primaryPlaylistsIds().first()
    val pageTokens = setOf("a", "b", "c", "d")
    val pagedPlaylist = Fake.pagedData(pageTokens) { currentToken, nextToken ->
        Fake.playlistItem(
            id = playlistsIds.likedVideos,
            nextPageToken = nextToken
        ).take(40).toList()
    }

    val userEndpoint = mockk<UserEndpoint>()
    val playlistEndpoint = mockk<PlaylistEndpoint>()
    val playlistDb = mockk<PlaylistItemDao>()

    private lateinit var viewModel: PlaylistItemsViewModel

    @Before
    fun setUp() {
        val repo = YoutubePlaylistRepository(
            userEndpoint,
            playlistEndpoint,
            playlistDb,
            testCoroutineRule.testDispatchers
        )
        viewModel = PlaylistItemsViewModel(repo, testCoroutineRule.testDispatchers)
    }

    @Test
    fun `should retrieve playlist items from network when database is empty`() =
        testCoroutineRule.testDispatcher.runBlockingTest {
            // The API endpoint returns a paginated playlist
            every { playlistEndpoint.getPlaylistById(any(), any()) } answers {
                pagedPlaylist[secondArg()] ?: error("Could not get playlist from endpoint")
            }
            // The values are inserted into the db
            coEvery { playlistDb.insertAll(any()) } just Runs

            every { playlistDb.playlistItemsFlow(playlistsIds.likedVideos) } returns flow {
                emit(emptyList())
                // Simulates network request
                delay(200)
                emit(pagedPlaylist[null]!!.data)
            }

            val selectedPlaylist = SelectedPlaylist.Extra(playlistsIds.likedVideos)

            // WHEN: A playlist is requested from the UI
            launch { viewModel.getPlaylist(selectedPlaylist) }

            // THEN: No values returns from the database
            Assert.assertTrue(viewModel.playlistItems.value.isEmpty())

            // A network request is made to retrieve more items
            verify(exactly = 1) { playlistEndpoint.getPlaylistById(any()) }

            // after the request returns
            advanceTimeBy(300)

            // THEN: the first page retrieved is available to the UI
            val playlistItems = viewModel.playlistItems.value
            Assert.assertTrue(playlistItems.size == 40)
    }
}