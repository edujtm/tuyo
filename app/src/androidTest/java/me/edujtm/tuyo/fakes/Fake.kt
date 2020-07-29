package me.edujtm.tuyo.fakes

import com.mooveit.library.Fakeit
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds
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

    fun playlistItem() = generateSequence {
        PlaylistItem(
            id = Fake.strings(size = 10).first(),
            channelId = Fake.strings(size = 10).first(),
            title = Fakeit.harryPotter().quote(),
            description = Fakeit.lorem().words(),
            playlistId = Fake.strings(size = 10).first(),
            videoId = Fake.strings(size = 10).first(),
            thumbnail = imageUrl().first()
        )
    }

    fun primaryPlaylistsIds() = generateSequence {
        PrimaryPlaylistsIds(
            likedVideos = Fake.strings(size = 10).first(),
            watchLater = Fake.strings(size = 10).first(),
            history = Fake.strings(size = 10).first(),
            favorites = Fake.strings(size = 10).first()
        )
    }
}

fun Int.isAsciiNumber() : Boolean = this in 48..57

fun Int.isAsciiUppercase(): Boolean = this in 65..90

fun Int.isAsciiLowercase(): Boolean = this in 97..122
