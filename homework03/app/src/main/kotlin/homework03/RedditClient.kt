package homework03

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import homework03.dto.CommentsSnapshot
import homework03.dto.JsonAboutWrapper
import homework03.dto.JsonPostsWrapper
import homework03.dto.TopicSnapshot
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*

object RedditClient {
    private val HTTP_CLIENT = HttpClient(CIO)
    private val OBJECT_MAPPER = ObjectMapper()
    private val MAIN_URL = "https://www.reddit.com/r/"
    private val COMM_URL = "/comments/"
    private val JSON_URL = "/.json"
    private val ABOUT_URL = "/about.json"

    init {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    suspend fun getTopic(name: String): TopicSnapshot {
        return TopicSnapshot.get(getAboutTopic(name), getPosts(name))
    }

    private suspend fun getAboutTopic(name: String): JsonAboutWrapper.TopicAbout {
        val json = HTTP_CLIENT.get(MAIN_URL + name + ABOUT_URL).body<String>()
        return OBJECT_MAPPER.readValue(json, JsonAboutWrapper::class.java).data
    }

    private suspend fun getPosts(name: String): JsonPostsWrapper.JsonPosts {
        val json = HTTP_CLIENT.get(MAIN_URL + name + JSON_URL).body<String>()
        return OBJECT_MAPPER.readValue(json, JsonPostsWrapper::class.java).data
    }

    suspend fun getComments(url: String): CommentsSnapshot {
        val json = HTTP_CLIENT.get(url + JSON_URL).body<String>()
        return CommentsSnapshot.parse(OBJECT_MAPPER, json)
    }

    suspend fun getComments(topicName: String, threadID: String): CommentsSnapshot {
        return getComments(MAIN_URL + topicName + COMM_URL + threadID)
    }
}