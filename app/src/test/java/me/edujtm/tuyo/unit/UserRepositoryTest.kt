package me.edujtm.tuyo.unit

import io.mockk.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import me.edujtm.tuyo.CoroutineTestRule
import me.edujtm.tuyo.Fake
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.domain.domainmodel.PrimaryPlaylist
import me.edujtm.tuyo.domain.domainmodel.PrimaryPlaylistsIds
import me.edujtm.tuyo.data.persistence.preferences.PrimaryPlaylistPreferences
import me.edujtm.tuyo.data.repository.YoutubeUserRepository
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class UserRepositoryTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule(TestCoroutineDispatcher())

    val playlistIds = Fake.primaryPlaylistsIds().first()
    val endpoint = mockk<UserEndpoint>()
    val cache = mockk<PrimaryPlaylistPreferences>()
    val repo = YoutubeUserRepository(
        endpoint,
        cache,
        coroutineRule.testDispatchers
    )

    @Test
    fun `should cache primary playlist ids from user`() =
        coroutineRule.testCoroutineScope.runBlockingTest {
            var cachedIds : PrimaryPlaylistsIds? = null

            coEvery { endpoint.getPrimaryPlaylistsIds() } returns playlistIds

            coEvery { cache.savePrimaryPlaylistIds(any()) } coAnswers {
                cachedIds = firstArg()
            }
            coEvery { cache.getPrimaryPlaylistId(any()) } coAnswers {
                cachedIds?.selectPlaylist(firstArg())
            }

            repeat(times = 2) {
                repo.getPrimaryPlaylistId(PrimaryPlaylist.LIKED_VIDEOS)
            }

            coVerify(exactly = 1) {
                endpoint.getPrimaryPlaylistsIds()
            }
        }

    @Test
    fun `should save primary playlists ids into cache`() =
        coroutineRule.testCoroutineScope.runBlockingTest {
            coEvery { endpoint.getPrimaryPlaylistsIds() } returns playlistIds
            coEvery { cache.savePrimaryPlaylistIds(any()) } just Runs

            // GIVEN: an empty cache
            coEvery { cache.getPrimaryPlaylistId(any()) } returns null

            // WHEN: retrieving the liked videos ID
            repo.getPrimaryPlaylistId(PrimaryPlaylist.LIKED_VIDEOS)

            // THEN: the ids retrieved from network should be saved on cache
            coVerify { cache.savePrimaryPlaylistIds(playlistIds) }
        }

    @Test
    fun `should retrieve ids from cache when available`() =
        coroutineRule.testCoroutineScope.runBlockingTest {
            coEvery { endpoint.getPrimaryPlaylistsIds() } returns playlistIds
            coEvery { cache.getPrimaryPlaylistId(PrimaryPlaylist.LIKED_VIDEOS) } coAnswers {
                playlistIds.likedVideos
            }

            val id = repo.getPrimaryPlaylistId(PrimaryPlaylist.LIKED_VIDEOS)

            coVerify { endpoint.getPrimaryPlaylistsIds() wasNot Called }
            Assert.assertEquals(id, playlistIds.likedVideos)
        }
}