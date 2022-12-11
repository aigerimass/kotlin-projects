package homework03

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*

object RedditClient {
    private val httpClient = HttpClient(CIO)

    suspend fun getTopic(name: String): String {
        return httpClient.get("https://www.reddit.com/r/$name/.json").body()
    }
}