package me.edujtm.tuyo

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.persistence.YoutubeDatabase
import me.edujtm.tuyo.domain.repository.YoutubePlaylistRepository
import org.junit.Before

@ExperimentalCoroutinesApi
@FlowPreview
class YoutubePlaylistRepositoryTest {

    val primaryPlaylists = Fake.primaryPlaylistsIds().first()

    val playlistPager = mockk<PlaylistEndpoint>()
    val userEndpoint = mockk<UserEndpoint>()
    private lateinit var database: YoutubeDatabase
    private lateinit var repo: YoutubePlaylistRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, YoutubeDatabase::class.java)
            .build()
        repo = YoutubePlaylistRepository(userEndpoint, playlistPager, database)
    }
}