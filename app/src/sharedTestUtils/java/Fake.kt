package me.edujtm.tuyo

import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds
import me.edujtm.tuyo.domain.domainmodel.PagedData
import kotlin.random.Random
import kotlin.random.nextInt

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

    fun playlistItem(id: String? = null, nextPageToken: String? = null) = generateSequence {
        PlaylistItem(
            id = id ?: strings(size = 10).first(),
            channelId = strings(size = 10).first(),
            title = strings(size = 20).first(),
            description = strings(size = 20).first(),
            playlistId = strings(size = 10).first(),
            videoId = strings(size = 10).first(),
            thumbnail = imageUrl().first(),
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

    fun <T> pagedData(tokens: Set<String>, pageInitializer: (String?, String?) -> T)
            : Map<String?, PagedData<T, String?>> {
        var currentToken : String? = null
        val pages = HashMap<String?, PagedData<T, String?>>()
        for (nextToken in tokens) {
            val data = pageInitializer(currentToken, nextToken)
            pages[currentToken] = PagedData(data, currentToken, nextToken)
            currentToken = nextToken
        }
        return pages
    }
}

fun Int.isAsciiNumber() : Boolean = this in 48..57

fun Int.isAsciiUppercase(): Boolean = this in 65..90

fun Int.isAsciiLowercase(): Boolean = this in 97..122
