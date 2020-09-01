package me.edujtm.tuyo.unit

import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import me.edujtm.tuyo.CoroutineTestRule
import me.edujtm.tuyo.Fake
import me.edujtm.tuyo.domain.domainmodel.PrimaryPlaylist
import me.edujtm.tuyo.domain.domainmodel.SelectedPlaylist
import me.edujtm.tuyo.domain.domainmodel.RequestState
import me.edujtm.tuyo.domain.repository.UserRepository
import me.edujtm.tuyo.data.repository.YoutubePlaylistRepository
import me.edujtm.tuyo.ui.playlistitems.PlaylistViewModel
import org.junit.Assert
import org.junit.Rule
import org.junit.Test


/**
 * These tests might be useless but I'm not experienced enough with testing to know.
 * I'll leave them here for now until I know better
 */
class PlaylistItemsViewModelTest {

    @get:Rule
    val testCoroutineRule = CoroutineTestRule(TestCoroutineDispatcher())

    val primaryPlaylistsIds = Fake.primaryPlaylistsIds().first()

    val playlists = mapOf(
        primaryPlaylistsIds.likedVideos to Fake.playlistItem(primaryPlaylistsIds.likedVideos).take(40).toList(),
        primaryPlaylistsIds.favorites to Fake.playlistItem(primaryPlaylistsIds.favorites).take(40).toList()
    )

    val playlistRepo = mockk<YoutubePlaylistRepository>()
    val userRepo = mockk<UserRepository>()
    val viewModel = PlaylistViewModel(playlistRepo, userRepo, testCoroutineRule.testDispatchers)

    @Test
    fun `getPrimaryPlaylist should set ui state when values are in cache`() =
        testCoroutineRule.testCoroutineScope.runBlockingTest {
            val selectedPlaylist = SelectedPlaylist.Extra(primaryPlaylistsIds.likedVideos)
            val playlistItems = playlists[primaryPlaylistsIds.likedVideos]!!

            // GIVEN: values available on the db cache
            every {
                playlistRepo.getPlaylist(any())
            } answers {
                flowOf(
                    playlists[firstArg()]
                        ?: error("Error on test setup: accessed non existent playlist")
                )
            }

            // WHEN: requesting a playlist by string ID
            viewModel.getPlaylist(selectedPlaylist)
            val result = viewModel.playlistItems.value.sucessfulItems

            verify { playlistRepo.getPlaylist(primaryPlaylistsIds.likedVideos) }
            Assert.assertTrue(playlistItems.hasEqualElementsTo(result) { it.id })
        }

    @Test
    fun `getPrimaryPlaylist should retrieve correct primary playlist`()
            = testCoroutineRule.testDispatcher.runBlockingTest {
        val selectedPlaylist = SelectedPlaylist.Primary(
            PrimaryPlaylist.LIKED_VIDEOS)
        val correctPlaylist = playlists[primaryPlaylistsIds.likedVideos]!!

        // GIVEN: The API returns the primary IDs correctly
        coEvery { userRepo.getPrimaryPlaylistId(any()) } coAnswers {
            primaryPlaylistsIds.selectPlaylist(firstArg())
        }

        // and values are available in the database cache
        every {
            playlistRepo.getPlaylist(any())
        } answers {
            flowOf(
                playlists[firstArg()]
                    ?: error("Error on test setup: accessed non existent playlist")
            )
        }

        // WHEN: request a primary playlist
        viewModel.getPlaylist(selectedPlaylist)
        val result = viewModel.playlistItems.value.sucessfulItems

        // THEN: the repository should provide the plalyist
        verify { playlistRepo.getPlaylist(any()) }
        // and the correct playlist is selected
        Assert.assertTrue(correctPlaylist.hasEqualElementsTo(result) { it.id })
    }

    @Test
    fun `getPrimaryPlaylist should request for more data when there are no values are in cache`()
        = testCoroutineRule.testDispatcher.runBlockingTest {
        val selectedPlaylist = SelectedPlaylist.Extra(primaryPlaylistsIds.likedVideos)

        // GIVEN: no values on the database
        every { playlistRepo.getPlaylist(any()) } returns flowOf(emptyList())
        coEvery { playlistRepo.requestPlaylistItems(primaryPlaylistsIds.likedVideos) } just Runs

        viewModel.getPlaylist(selectedPlaylist)

        coVerify { playlistRepo.requestPlaylistItems(primaryPlaylistsIds.likedVideos) }
    }


    private fun <T, R> List<T>.hasEqualElementsTo(other: List<T>, compareBy: ((T) -> R)?) : Boolean {
        if (size != other.size) return false
        for (value in this.zip(other)) {
            val first = compareBy?.invoke(value.first) ?: value.first
            val second = compareBy?.invoke(value.second) ?: value.second
            if (first != second) {
                return false
            }
        }
        return true
    }

    private val <T> RequestState<T>.sucessfulItems : T
        get() = (this as RequestState.Success).data
}