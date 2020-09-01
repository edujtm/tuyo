package me.edujtm.tuyo.data.repository

import com.google.api.services.youtube.YouTube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.edujtm.tuyo.domain.domainmodel.RequestState
import me.edujtm.tuyo.domain.repository.VideoRepository
import javax.inject.Inject


class YoutubeVideoRepository
    @Inject constructor(val youtube: YouTube) :
    VideoRepository {

    override suspend fun getVideoInfo(): RequestState<List<String>> = withContext(Dispatchers.IO) {
        val channelInfo = arrayListOf<String>()
        try {
            val result = youtube.channels().list("snippet,contentDetails,statistics")
                .setForUsername("GoogleDevelopers")
                .execute()

            val channels = result.items
            if (channels != null) {
                val channel = channels[0]
                channelInfo.add("""
                    |This channel's ID is ${channel.id}.
                    |Its title is ${channel.snippet.title},
                    |and it has ${channel.statistics.viewCount} views.
                """.trimMargin())
            }
            RequestState.Success(channelInfo)
        } catch (e: Exception) {
             RequestState.Failure(e)
        }
    }

}

