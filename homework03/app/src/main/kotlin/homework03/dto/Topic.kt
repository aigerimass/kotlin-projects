package homework03.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class TopicSnapshot(
    val publishDate: Date,
    val activeSubscribers: Long,
    val description: String,
    val posts: List<Post>
) {
    val downloadTime = Date(System.currentTimeMillis())

    companion object {
        fun get(topicAbout: JsonAboutWrapper.TopicAbout, JsonPosts: JsonPostsWrapper.JsonPosts) = TopicSnapshot(
            publishDate = Date(topicAbout.createdEpochTime.toLong() * 1000),
            activeSubscribers = topicAbout.activeUserCount,
            description = topicAbout.publicDescription,
            posts = JsonPosts.jsonPostWrappers.map {it.data}
        )
    }
}

data class JsonAboutWrapper(@JsonProperty("data") val data: TopicAbout) {
    data class TopicAbout(
        @JsonProperty("created") val createdEpochTime: Double,
        @JsonProperty("active_user_count") val activeUserCount: Long,
        @JsonProperty("ranking_size") val rankingSize: String?,
        @JsonProperty("public_description") val publicDescription: String
    )
}

data class Post(
    @JsonProperty("author") val author: String,
    @JsonProperty("created") val createdEpochTime: Double,
    @JsonProperty("ups") val upVotes: Long,
    @JsonProperty("downs") val downVotes: Long,
    @JsonProperty("title") val title: String,
    @JsonProperty("selftext") val selftext: String?,
    @JsonProperty("selftext_html") val selfhtmlText: String?,
    @JsonProperty("id") val id: String
)

data class JsonPostsWrapper(@JsonProperty("data") val data: JsonPosts) {
    data class JsonPosts(@JsonProperty("children") val jsonPostWrappers: List<JsonPostWrapper>) {
        data class JsonPostWrapper(@JsonProperty("data") val data: Post)
    }
}

