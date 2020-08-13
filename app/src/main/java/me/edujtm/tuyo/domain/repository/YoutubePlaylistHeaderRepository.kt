package me.edujtm.tuyo.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.edujtm.tuyo.auth.AuthManager
import me.edujtm.tuyo.auth.GoogleAccount
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.model.PlaylistHeaderDB
import me.edujtm.tuyo.data.model.PlaylistHeaderJson
import me.edujtm.tuyo.data.persistence.PlaylistHeaderDao
import me.edujtm.tuyo.domain.DispatcherProvider
import me.edujtm.tuyo.domain.Mapper
import me.edujtm.tuyo.domain.domainmodel.PagedData
import me.edujtm.tuyo.domain.domainmodel.PlaylistHeader
import javax.inject.Inject

class YoutubePlaylistHeaderRepository
    @Inject constructor(
        val userEndpoint: UserEndpoint,
        val playlistHeaderDb: PlaylistHeaderDao,
        val authManager: AuthManager,
        val dispatchers: DispatcherProvider
    ) : PlaylistHeaderRepository {

    override suspend fun requestPlaylistHeaders(token: String?) =
        withContext(dispatchers.io) {
            val result = userEndpoint.getUserPlaylists(token)

            val dbItems = requireUser("Download playlist headers from network") { user ->
                result.data
                    .map { it to user.id }   // This is just getting context information to the function below
                    .map(networkToDatabase)   // I need the user ID when instantiating DB instances
            }

            playlistHeaderDb.insertAll(dbItems)
        }

    override fun getUserPlaylists() : Flow<List<PlaylistHeader>> =
        requireUser("Retrieve playlist headers from database") { user ->
            playlistHeaderDb.getUserPlaylists(user.id)
                .map { dbItems ->
                    dbItems.map(databaseToDomain)
                }
        }

    private fun <T> requireUser(description: String? = null, withUserAction: (GoogleAccount) -> T) : T {
        val user = authManager.getUserAccount()
        if (user != null) {
            return withUserAction(user)
        } else {
            var message = "Could not retrieve user."
            description?.let {
                message.trimEnd('.')
                message += " for following operation: $description."
            }
            throw IllegalStateException(message)
        }
    }

    private val networkToDatabase : Mapper<Pair<PlaylistHeaderJson, String>, PlaylistHeaderDB> =
        { context ->
            val (json, userId) = context
            PlaylistHeaderDB(
                id = json.id,
                ownerId = userId,
                title = json.title,
                itemCount = json.itemCount,
                thumbnailUrl = json.thumbnail,
                publishedAt = json.publishedAt,
                nextPageToken = json.nextPageToken
            )
        }

    private val databaseToDomain : Mapper<PlaylistHeaderDB, PlaylistHeader> = { dbItem ->
        PlaylistHeader(
            id = dbItem.id,
            title = dbItem.title,
            itemCount = dbItem.itemCount,
            thumbnailUrl = dbItem.thumbnailUrl,
            publishedAt = dbItem.publishedAt,
            nextPageToken = dbItem.nextPageToken
        )
    }
}