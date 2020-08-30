package me.edujtm.tuyo.fakes

import android.content.ContentResolver
import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import me.edujtm.tuyo.Fake
import me.edujtm.tuyo.R
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.model.PlaylistItemJson
import me.edujtm.tuyo.di.scopes.PerUserSession
import me.edujtm.tuyo.domain.domainmodel.PlaylistItem
import me.edujtm.tuyo.domain.domainmodel.PagedData
import me.edujtm.tuyo.domain.domainmodel.Playlist
import me.edujtm.tuyo.domain.domainmodel.Token
import javax.inject.Inject

@PerUserSession
class FakePlaylistEndpoint
    @Inject constructor() : PlaylistEndpoint {

    private val cache = mutableMapOf<String, List<PlaylistItemJson>>()

    override suspend fun getPlaylistById(
        id: String,
        token: Token?,
        pageSize: Long
    ): PagedData<PlaylistItemJson> {
        val playlist = cache.getOrPut(id) {
            Fake.Network
                .playlistItem(playlistId = id, imageUrl = getDrawableUri(), nextPageToken = null)
                .take(pageSize.toInt())
                .toList()
        }

        return PagedData(
            data = playlist,
            nextPageToken = null as String?,
            prevPageToken = null as String?
        )
    }

    private fun getDrawableUri() : String {
        val resources = InstrumentationRegistry.getInstrumentation().context.resources

        val imageUri = Uri.parse(
            "${ContentResolver.SCHEME_ANDROID_RESOURCE}://" +
                    "${resources.getResourcePackageName(R.mipmap.ic_launcher)}/" +
                    "${resources.getResourceTypeName(R.mipmap.ic_launcher)}/" +
                    resources.getResourceEntryName(R.mipmap.ic_launcher)
        )

        return imageUri.toString()
    }
}