package me.edujtm.tuyo.integration

import io.mockk.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import me.edujtm.tuyo.CoroutineTestRule
import me.edujtm.tuyo.Fake
import me.edujtm.tuyo.InMemoryPlaylistItemDao
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.model.PrimaryPlaylist
import me.edujtm.tuyo.data.model.SelectedPlaylist
import me.edujtm.tuyo.data.persistence.preferences.PrimaryPlaylistPreferences
import me.edujtm.tuyo.domain.domainmodel.RequestState
import me.edujtm.tuyo.domain.repository.YoutubePlaylistRepository
import me.edujtm.tuyo.domain.repository.YoutubeUserRepository
import me.edujtm.tuyo.ui.playlistitems.PlaylistViewModel
import org.junit.*
import java.lang.RuntimeException

/**
 * These tests are for the whole Playlist Item infrastructure which includes ViewModel,
 * Repository and data layer. They test if the assumptions I have from the UI layer are
 * replicated on the data access layer.
 */
class PlaylistItemsTest {

    @get:Rule
    val testCoroutineRule = CoroutineTestRule(TestCoroutineDispatcher())

    val playlistsIds = Fake.primaryPlaylistsIds().first()

    val PAGE_SIZE = 40
    val pageTokens = setOf("a", "b", "c", "d")
    val pagedPlaylist = Fake.pagedData(pageTokens) { _, nextToken ->
        Fake.Network.playlistItem(
            playlistId = playlistsIds.likedVideos,
            nextPageToken = nextToken
        ).take(PAGE_SIZE).toList()
    }

    val userEndpoint = mockk<UserEndpoint>()
    val playlistEndpoint = mockk<PlaylistEndpoint>()
    val cache = mockk<PrimaryPlaylistPreferences>()

    private lateinit var playlistDb : InMemoryPlaylistItemDao
    private lateinit var viewModel : PlaylistViewModel

    @Before
    fun setUp() {
        playlistDb = InMemoryPlaylistItemDao()
        playlistDb.onStart()
        val playlistRepo = YoutubePlaylistRepository(
            playlistEndpoint,
            playlistDb,
            testCoroutineRule.testDispatchers
        )
        val userRepo = YoutubeUserRepository(userEndpoint, cache)
        viewModel = PlaylistViewModel(playlistRepo, userRepo, testCoroutineRule.testDispatchers)
    }

    @After
    fun tearDown() {
        playlistDb.onDestroy()
    }

    @Test
    fun `should retrieve playlist items from network when database is empty`() =
        testCoroutineRule.testCoroutineScope.runBlockingTest {
            // The API endpoint returns a paginated playlist
            coEvery { playlistEndpoint.getPlaylistById(any(), any()) } coAnswers {
                // Simulates a network request
                delay(300)
                pagedPlaylist[secondArg()] ?: error("Test error: could not get paginated data")
            }

            val selectedPlaylist = SelectedPlaylist.Extra(playlistsIds.likedVideos)

            // WHEN: A playlist is requested from the UI
             val job = launch { viewModel.getPlaylist(selectedPlaylist) }

            // var playlistItems = (viewModel.playlistItems.value as RequestState.Success).data
            // THEN: No values returns from the database initially
            Assert.assertTrue(viewModel.playlistItems.value is RequestState.Success)

            // A network request is made to retrieve more items
            coVerify(exactly = 1) { playlistEndpoint.getPlaylistById(any()) }

            // after the request returns
            advanceTimeBy(300)

            // THEN: the first page retrieved is available to the UI
            val playlistItems = (viewModel.playlistItems.value as RequestState.Success).data
            Assert.assertTrue(
                "Wrong playlist items size after network request",
                playlistItems.size == PAGE_SIZE
            )

            // The database flow emits forever, so it needs to be cancelled
            job.cancel()
            job.join()
        }

    @Test
    fun `should retrieve primary playlists ids when given a SelectedPlaylist Primary ID`() =
        testCoroutineRule.testCoroutineScope.runBlockingTest {
            coEvery { playlistEndpoint.getPlaylistById(any(), any()) } coAnswers {
                pagedPlaylist[secondArg()] ?: error("Test error: could not get paginated data")
            }

            // GIVEN: No value cached
            coEvery { cache.getPrimaryPlaylistId(PrimaryPlaylist.LIKED_VIDEOS) } returns null
            coEvery { userEndpoint.getPrimaryPlaylistsIds() } returns playlistsIds

            // WHEN: user specifies a primary playlist
            val selectedPlaylist = SelectedPlaylist.Primary(PrimaryPlaylist.LIKED_VIDEOS)
            val job = launch { viewModel.getPlaylist(selectedPlaylist) }

            // The primary playlist ids should be retrieved
            coVerify {
                userEndpoint.getPrimaryPlaylistsIds()
            }

            // The database flow emits forever, so it needs to be cancelled
            job.cancel()
            job.join()
        }


    @Test
    fun `should retrieve all pages when calling requestPlaylistItems`() =
        testCoroutineRule.testCoroutineScope.runBlockingTest {
            coEvery { playlistEndpoint.getPlaylistById(any(), any()) } coAnswers {
                delay(100)
                pagedPlaylist[secondArg()] ?: error("Test error: could not get paginated data")
            }

            // The user selects some playlist
            val selectedPlaylist = SelectedPlaylist.Extra(playlistsIds.likedVideos)

            val uiJob = launch {
                viewModel.getPlaylist(selectedPlaylist)
            }

            // WHEN: request are made for different page tokens
            val allTokens = pageTokens + (null as String?)
            launch {
                for (token in allTokens) {
                    viewModel.requestPlaylistItems(selectedPlaylist, token)
                }
            }

            Assert.assertTrue(
                "Wrong playlist items initial size",
                viewModel.playlistItems.value.sucessfulItems.isEmpty()
            )

            // After all the request are Done
            advanceTimeBy(500)

            Assert.assertTrue(
                "Wrong playlist items size after retrieving from network",
                viewModel.playlistItems.value.sucessfulItems.size == allTokens.size * PAGE_SIZE
            )

            // The database emits forever so it needs to be cancelled
            uiJob.cancel()
            uiJob.join()
        }

    @Test
    fun `should transmit API errors to UI layer`() =
        testCoroutineRule.testCoroutineScope.runBlockingTest {
            coEvery { playlistEndpoint.getPlaylistById(any(), any()) } throws RuntimeException("API error")

            val selectedPlaylist = SelectedPlaylist.Extra(playlistsIds.likedVideos)
            val uiJob = launch {
                viewModel.requestPlaylistItems(selectedPlaylist)
            }

            Assert.assertTrue(
                "Error was not received from the API",
                viewModel.playlistItems.value is RequestState.Failure
            )

            // The database emits forever so it needs to be cancelled
            uiJob.cancel()
            uiJob.join()
    }

    @Test
    fun `getPlaylist should not expose exceptions to UI`() =
        testCoroutineRule.testCoroutineScope.runBlockingTest {
            // GIVEN: No value in cache
            coEvery { cache.getPrimaryPlaylistId(PrimaryPlaylist.LIKED_VIDEOS) } returns null
            // GIVEN: a network error happens
            coEvery { userEndpoint.getPrimaryPlaylistsIds() } throws RuntimeException("API error")

            val selectedPlaylist = SelectedPlaylist.Primary(PrimaryPlaylist.LIKED_VIDEOS)
            val uiJob = launch {
                viewModel.getPlaylist(selectedPlaylist)
            }

            // THEN: the API is called
            coVerify { userEndpoint.getPrimaryPlaylistsIds() }

            // A failure is transmitted to the UI
            Assert.assertTrue(
                "Error was not received from the API",
                viewModel.playlistItems.value is RequestState.Failure
            )

            // No exception is exposed to UI

            // The database emits forever so it needs to be cancelled
            uiJob.cancel()
            uiJob.join()
        }

    private val <T> RequestState<T>.sucessfulItems : T
        get() = (this as RequestState.Success).data
}