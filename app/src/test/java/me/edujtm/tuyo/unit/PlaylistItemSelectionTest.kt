package me.edujtm.tuyo.unit

import io.mockk.mockk
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import me.edujtm.tuyo.CoroutineTestRule
import me.edujtm.tuyo.Fake
import me.edujtm.tuyo.data.repository.YoutubePlaylistRepository
import me.edujtm.tuyo.domain.repository.UserRepository
import me.edujtm.tuyo.ui.playlistitems.PlaylistViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PlaylistItemSelectionTest {
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
    fun `clicking an item in normal mode should not select it`() =
        testCoroutineRule.testCoroutineScope.runBlockingTest {
            val playlist = playlists[primaryPlaylistsIds.likedVideos] ?: error("Couldn't retrieve playlist items")

            // GIVEN: the viewmodel is not in select mode
            viewModel.setInSelectMode(false)

            // WHEN: clicking an item
            viewModel.clickItem(playlist[0])

            // THEN: No items should be selected
            assertEquals(
                "Incorrect selected playlist ids after clicking item",
                emptySet<String>(),
                viewModel.selectedItems.value
            )
        }

    @Test
    fun `long click on item in normal mode should start selected mode`() {
        val playlist = playlists[primaryPlaylistsIds.likedVideos] ?: error("Couldn't retrieve playlist items")

        // GIVEN: the viewmodel is in normal mode
        viewModel.setInSelectMode(false)

        // WHEN: long clicking an item
        viewModel.longClickItem(playlist[0])

        // THEN: The item should be selected
        assertEquals(
            "Incorrect selected playlist ids after long click",
            setOf(playlist[0].id),
            viewModel.selectedItems.value
        )

        // THEN: the viewmodel should be in select mode
        assertTrue(
            "ViewModel not in select mode",
            viewModel.inSelectMode.value
        )
    }

    @Test
    fun `after entering select mode, normal clicks select items`() {
        val playlist = playlists[primaryPlaylistsIds.likedVideos] ?: error("Couldn't retrieve playlist items")

        // GIVEN: the viewmodel is in select mode
        viewModel.setInSelectMode(false)
        viewModel.longClickItem(playlist[0])

        repeat(3) { i ->
           viewModel.clickItem(playlist[i+1])
        }

        // THEN: All items should be selected
        val ids = playlist.take(4).map { it.id }
        assertEquals(
            "Incorrect selected playlist items ids",
            ids.toSet(),
            viewModel.selectedItems.value
        )
    }

    @Test
    fun `clicking item in select mode should not trigger click command`() =
        testCoroutineRule.testCoroutineScope.runBlockingTest {
            val playlist = playlists[primaryPlaylistsIds.likedVideos] ?: error("Couldn't retrieve playlist items")

            var commands = 0

            val job = launch {
                viewModel.playlistClickCommand.consumeEach {
                    commands++
                }
            }

            // GIVEN: the view model is in select mode
            viewModel.setInSelectMode(true)

            // WHEN: clicking an item
            viewModel.clickItem(playlist[0])

            // THEN: no action should be received
            assertEquals(
                "Click command was sent but was not expected",
                0,
                commands
            )

            job.cancelAndJoin()
        }

    @Test
    fun `deselecting all items in select mode should disable select mode`() {
        val playlist = playlists[primaryPlaylistsIds.likedVideos] ?: error("Couldn't retrieve playlist items")
        val playlistItems = playlist.take(4).toSet()

        // GIVEN: the view model is in select mode with 4 items clicked
        viewModel.setInSelectMode(true)
        for (item in playlistItems) {
            viewModel.clickItem(item)
        }

        // WHEN: Deselecting all items
        for (item in playlistItems) {
            viewModel.clickItem(item)
        }

        // THEN: the view model should be in normal mode
        assertEquals(
            "PlaylistViewModel was not in normal mode after deselecting all items",
            false,
            viewModel.inSelectMode.value
        )

        // THEN: the selected items should be empty
        assertTrue(
            "view model still has selected items after quitting select mode",
            viewModel.selectedItems.value.isEmpty()
        )
    }
}