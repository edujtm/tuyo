package me.edujtm.tuyo

import androidx.paging.ExperimentalPagingApi
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.model.PrimaryPlaylist
import me.edujtm.tuyo.domain.paging.PageSource
import me.edujtm.tuyo.domain.repository.YoutubePlaylistRepository
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@FlowPreview
class YoutubePlaylistRepositoryTest {

    val primaryPlaylists = Fake.primaryPlaylistsIds().first()

    val playlistPager = mockk<PageSource<String, Int>>()
    val userEndpoint = mockk<UserEndpoint>()

    val repo = YoutubePlaylistRepository(userEndpoint, playlistPager)

    @Test
    fun `getPrimaryPlaylist() should call user endpoint then forward result to pager`() = runBlockingTest {
        every { userEndpoint.getPrimaryPlaylistsIds() } returns primaryPlaylists
        every { playlistPager.getPages(any()) } returns flowOf(1,2, 3)

        val result = mutableListOf<Int>()
        // WHEN: getPrimaryPlaylist is called
        repo.getPrimaryPlaylist(PrimaryPlaylist.LIKED_VIDEOS)
            .onEach { result += it }
            .launchIn(this)

        // THEN: the pager should be called with the result from the user endpoint
        verify { playlistPager.getPages(primaryPlaylists.likedVideos) }
        Assert.assertArrayEquals(arrayOf(1,2,3), result.toTypedArray())
    }

    @Test
    fun `UserEndpoint error should be propagated downstream`() = runBlockingTest {
        every { userEndpoint.getPrimaryPlaylistsIds() } throws IllegalStateException("Could not retrieve primary playlists IDs")
        every { playlistPager.getPages(any()) } returns flowOf(1,2, 3)

        val result = try {
            repo.getPrimaryPlaylist(PrimaryPlaylist.LIKED_VIDEOS).collect()
            "Everything went ok"
        } catch (e: IllegalStateException) {
            e.message
        }

        Assert.assertEquals("Could not retrieve primary playlists IDs", result)
    }
}