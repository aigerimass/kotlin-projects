package homework03.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class JsonMetaInfoWrapper(@JsonProperty("data") val topicAbout: TopicAbout) {
    data class TopicAbout(
        @JsonProperty("created") val createdEpochTime: Double,
        @JsonProperty("active_user_count") val activeUserCount: Long,
        @JsonProperty("ranking_size") val rankingSize: String?,
        @JsonProperty("public_description") val publicDescription: String
    )
}

data class TopicSnapshot(
    val publishDate: Date,
    val activeSubscribers: Long,
    val sizeRanking: String?,
    val description: String
) {
    companion object {
        fun get(metaInfo: JsonMetaInfoWrapper) = TopicSnapshot(
            publishDate = Date(metaInfo.topicAbout.createdEpochTime.toLong() * 1000),
            activeSubscribers = metaInfo.topicAbout.activeUserCount,
            sizeRanking = metaInfo.topicAbout.rankingSize,
            description = metaInfo.topicAbout.publicDescription
        )
    }
}