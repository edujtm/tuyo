package me.edujtm.tuyo.integration

import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import me.edujtm.tuyo.CoroutineTestRule
import me.edujtm.tuyo.Fake
import me.edujtm.tuyo.InMemoryPlaylistHeaderDao
import me.edujtm.tuyo.auth.AuthManager
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.domain.domainmodel.RequestState
import me.edujtm.tuyo.domain.repository.PlaylistHeaderRepository
import me.edujtm.tuyo.domain.repository.YoutubePlaylistHeaderRepository
import me.edujtm.tuyo.ui.home.HomeViewModel
import org.junit.*


@ExperimentalCoroutinesApi
@FlowPreview
class PlaylistHeaderTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule(TestCoroutineDispatcher())

    val tokens = setOf("a", "b", "c", "b")
    val PAGE_SIZE = 20
    val pages = Fake.pagedData(tokens) { currentToken, nextToken ->
        Fake.Network.playlistHeader(nextPageToken = nextToken).take(PAGE_SIZE).toList()
    }

    private val userEndpoint = mockk<UserEndpoint>()
    val authManager = mockk<AuthManager>()

    private lateinit var headerDb: InMemoryPlaylistHeaderDao
    private lateinit var viewModel : HomeViewModel

    @Before
    fun setUp() {
        headerDb = InMemoryPlaylistHeaderDao()
        headerDb.onStart()
        val repo = YoutubePlaylistHeaderRepository(
            userEndpoint,
            headerDb,
            authManager,
            coroutineRule.testDispatchers
        )
        viewModel = HomeViewModel(repo, coroutineRule.testDispatchers)
    }

    @After
    fun tearDown() {
        headerDb.onDestroy()
    }

    @Test
    fun `getUserPlaylists should get a page of headers from API when DB is empty`() =
        coroutineRule.testDispatcher.runBlockingTest {
            every { authManager.getUserAccount() } returns Fake.FAKE_ACCOUNT

            coEvery { userEndpoint.getUserPlaylists(any()) } coAnswers {
                delay(300)
                pages[firstArg()] ?: error("Test error: could not get paged playlist headers")
            }

            // WHEN: the UI asks for the playlists
            val job = launch { viewModel.getUserPlaylists() }

            // No items are received from DB
            var items = (viewModel.playlistHeaders.value as RequestState.Success)
            Assert.assertTrue(
                "HomeViewModel did not start with empty playlist headers",
                items.data.isEmpty()
            )

            // The API is then called
            coVerify(exactly = 1) { userEndpoint.getUserPlaylists() }

            // After the network response is received
            advanceTimeBy(300)

            // THEN: the results should be available for the UI layer
            items = (viewModel.playlistHeaders.value as RequestState.Success)
            Assert.assertTrue(
                "Playlist quantity after network loading is not correct",
                items.data.size == PAGE_SIZE
            )

            job.cancel()
            job.join()
        }

    @Test
    fun `getUserPlaylists should not call API if the values are available in the DB`() =
        coroutineRule.testDispatcher.runBlockingTest {
            every { authManager.getUserAccount() } returns Fake.FAKE_ACCOUNT

            val dbItems = Fake.Database.playlistHeader(
                userId = Fake.FAKE_ACCOUNT.id
            ).take(PAGE_SIZE).toList()

            // GIVEN: database already has items cached
            headerDb.insertAll(dbItems)

            // WHEN: the UI request a playlist
            val job = launch { viewModel.getUserPlaylists() }

            // THEN: The API is not queried
            coVerify(exactly = 0) { userEndpoint.getUserPlaylists() }

            // THEN: The UI receives the data from database
            Assert.assertTrue(
                "The playlist headers were not retrieved from the database",
                (viewModel.playlistHeaders.value as RequestState.Success).data.size == PAGE_SIZE
            )

            job.cancel()
            job.join()
        }

    @Test
    fun `requestPlaylistHeaders should retrieve all pages from network`() =
        coroutineRule.testDispatcher.runBlockingTest {
            every { authManager.getUserAccount() } returns Fake.FAKE_ACCOUNT

            coEvery { userEndpoint.getUserPlaylists(any()) } coAnswers {
                delay(100)
                pages[firstArg()] ?: error("Test error: could not get paged playlist headers")
            }

            // GIVEN: The ui is connected to the DB
            val uiJob = launch {
                viewModel.getUserPlaylists()
            }

            val allTokens = tokens + (null as String?)
            launch {
                for (token in allTokens) {
                    viewModel.requestPlaylistHeaders(token)
                }
            }

            var items = (viewModel.playlistHeaders.value as RequestState.Success)
            Assert.assertTrue(
                "Wrong playlist headers initial size",
                items.data.isEmpty()
            )

            // After the request are retrieved
            advanceTimeBy(500)

            items = (viewModel.playlistHeaders.value as RequestState.Success)
            // THEN: all pages should be retrieved
            Assert.assertTrue(
                "Wrong playlist headers size after retrieving pages from network",
                items.data.size == PAGE_SIZE * allTokens.size
            )

            uiJob.cancel()
            uiJob.join()
        }

    @Ignore("This test would be interesting if I had a way to count updates on the StateFlow")
    @Test
    fun `getUserPlaylists should not open multiple subscriptions to DB items`() =
        coroutineRule.testDispatcher.runBlockingTest {
            // SETUP
            every { authManager.getUserAccount() } returns Fake.FAKE_ACCOUNT

            coEvery { userEndpoint.getUserPlaylists(any()) } answers {
                pages[firstArg()] ?: error("Test error: could not get paged playlist headers")
            }

            var stateUpdates = 0
            val job = launch {
                viewModel.playlistHeaders
                    .drop(1)    // Don't care about initial state, only state changes
                    .collectLatest {
                        stateUpdates++
                    }
            }

            // WHEN: calling getUserPlaylists for the first time
            viewModel.getUserPlaylists()

            // THEN: There should be only one update
            Assert.assertEquals(
                "Only one state update should happen when requesting playlist headers for the first time",
                1, stateUpdates
            )

            // WHEN: subscribing a second time (which might happen due to lifecycle differences)
            viewModel.getUserPlaylists()

            // WHEN: and an DB state change happens
            val dbItems = Fake.Database.playlistHeader(userId = Fake.FAKE_ACCOUNT.id).take(PAGE_SIZE).toList()
            headerDb.insertAll(dbItems)

            Assert.assertEquals(
                "Only two state updates should happen when requesting playlist headers again",
                2, stateUpdates
            )

            job.cancel()
            job.join()
        }
}