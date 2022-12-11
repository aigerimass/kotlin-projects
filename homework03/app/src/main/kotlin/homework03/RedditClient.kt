package homework03

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import homework03.dto.JsonAboutWrapper
import homework03.dto.JsonPostsWrapper
import homework03.dto.TopicSnapshot
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*

object RedditClient {
    private val httpClient = HttpClient(CIO)
    private val objectMapper = ObjectMapper()

    init {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    suspend fun getTopic(name: String): TopicSnapshot {
        return TopicSnapshot.get(getAboutTopic(name), getPosts(name))
    }

    private suspend fun getAboutTopic(name: String): JsonAboutWrapper.TopicAbout {
        val aboutTopicJson = httpClient.get("https://www.reddit.com/r/$name/about.json").body<String>()
        return objectMapper.readValue(aboutTopicJson, JsonAboutWrapper::class.java).data
    }

    private suspend fun getPosts(name: String): JsonPostsWrapper.JsonPosts {
        val postsJson = httpClient.get("https://www.reddit.com/r/$name/.json").body<String>()
        return objectMapper.readValue(postsJson, JsonPostsWrapper::class.java).data
    }
}