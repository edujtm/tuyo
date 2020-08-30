package me.edujtm.tuyo

import me.edujtm.tuyo.auth.GoogleAccount
import me.edujtm.tuyo.data.model.*
import me.edujtm.tuyo.domain.domainmodel.PlaylistItem
import me.edujtm.tuyo.domain.domainmodel.PagedData
import me.edujtm.tuyo.domain.domainmodel.PlaylistHeader
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

object Fake {

    private val random = Random(123)

    fun alphanumeric() = generateSequence {
        // From '0' to 'z' in ASCII
        random.nextInt(48..122)
    }.filter { it.isAsciiNumber() || it.isAsciiLowercase() || it.isAsciiUppercase() }

    fun strings(size: Int) = generateSequence {
        alphanumeric().take(size)
            .map { it.toChar() }
            .joinToString(separator = "")
    }

    fun imageUrl() = generateSequence {
        "https://fakeimg.pl/120x90/"
    }

    fun playlistItem(playlistId: String? = null, nextPageToken: String? = null) = generateSequence {
        PlaylistItem(
            id = strings(size = 10).first(),
            channelId = strings(size = 10).first(),
            title = strings(size = 20).first(),
            description = strings(size = 20).first(),
            playlistId = playlistId ?: strings(size = 10).first(),
            videoId = strings(size = 10).first(),
            thumbnailUrl = imageUrl().first(),
            nextPageToken = nextPageToken
        )
    }

    fun primaryPlaylistsIds() = generateSequence {
        PrimaryPlaylistsIds(
            likedVideos = strings(size = 10).first(),
            watchLater = strings(size = 10).first(),
            history = strings(size = 10).first(),
            favorites = strings(size = 10).first()
        )
    }

    fun <T> pagedData(tokens: Set<String>, pageInitializer: (String?, String?) -> List<T>)
            : Map<String?, PagedData<T>> {
        var currentToken : String? = null
        val pages = HashMap<String?, PagedData<T>>()

        // Add first and middle pages
        for (nextToken in tokens) {
            val data = pageInitializer(currentToken, nextToken)
            pages[currentToken] = PagedData(data, currentToken, nextToken)
            currentToken = nextToken
        }

        // Last page has null as nextToken
        val lastPage = pageInitializer(currentToken, null)
        pages[currentToken] = PagedData(lastPage, currentToken, null)
        return pages
    }

    val FAKE_ACCOUNT = GoogleAccount(
        id = "super-random-id",
        email = "example.user@gmail.com",
        displayName = "Eduardo Macedo",
        photoUrl = "https://placekitten.com/200/200"
    )

    object Domain {
        fun playlistHeader(token: String? = null) = generateSequence {
            PlaylistHeader(
                id = strings(size = 10).first(),
                title = strings(size = 10).first(),
                thumbnailUrl = imageUrl().first(),
                publishedAt = strings(size = 10).first(),
                itemCount = random.nextLong(10L..100L),
                nextPageToken = token ?: strings(size = 6).first()
            )
        }
    }

    object Network {
        fun playlistHeader(nextPageToken: String? = null) = generateSequence {
            PlaylistHeaderJson(
                id = strings(size = 10).first(),
                title = strings(size = 10).first(),
                itemCount = random.nextLong(10L..100L),
                publishedAt = strings(size= 10).first(),
                thumbnail = imageUrl().first(),
                nextPageToken = nextPageToken ?: strings(size = 6).first()
            )
        }

        fun playlistItem(playlistId: String? = null, nextPageToken: String? = null, imageUrl : String? = null) = generateSequence {
            PlaylistItemJson(
                id = strings(size = 10).first(),
                channelId = strings(size = 10).first(),
                title = strings(size = 20).first(),
                description = strings(size = 20).first(),
                playlistId = playlistId ?: strings(size = 10).first(),
                videoId = strings(size = 10).first(),
                thumbnailUrl = imageUrl ?: imageUrl().first(),
                nextPageToken = nextPageToken
            )
        }
    }

    object Database {
        fun playlistHeader(userId: String? = null, token: String? = null) = generateSequence {
            PlaylistHeaderDB(
                id = strings(size = 10).first(),
                ownerId = userId ?: strings(size = 10).first(),
                title = strings(size = 10).first(),
                itemCount = random.nextLong(10L..100L),
                publishedAt = strings(size= 10).first(),
                thumbnailUrl = imageUrl().first(),
                nextPageToken = token ?: strings(size = 6).first()
            )
        }

        fun playlistItem(playlistId: String? = null, nextPageToken: String? = null) = generateSequence {
            PlaylistItemDB(
                id = strings(size = 10).first(),
                channelId = strings(size = 10).first(),
                title = strings(size = 20).first(),
                description = strings(size = 20).first(),
                playlistId = playlistId ?: strings(size = 10).first(),
                videoId = strings(size = 10).first(),
                thumbnailUrl = imageUrl().first(),
                nextPageToken = nextPageToken
            )
        }
    }
}

fun Int.isAsciiNumber() : Boolean = this in 48..57

fun Int.isAsciiUppercase(): Boolean = this in 65..90

fun Int.isAsciiLowercase(): Boolean = this in 97..122
