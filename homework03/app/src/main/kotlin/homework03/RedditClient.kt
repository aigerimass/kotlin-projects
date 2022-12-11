package homework03

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import homework03.dto.JsonMetaInfoWrapper
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
        val metaInfoJson = httpClient.get("https://www.reddit.com/r/$name/about.json").body<String>()
        val topicMainData = objectMapper.readValue(metaInfoJson, JsonMetaInfoWrapper::class.java)
        return TopicSnapshot.get(topicMainData)
    }
}