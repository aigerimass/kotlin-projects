package homework03.dto

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

var countComments: Long = 0

data class Comment(
    val creationTime: Date,
    val ups: Long,
    val downs: Long,
    val text: String,
    val author: String,
    val replyTo: Long?,
    val replies: List<Long>,
    val depth: Int,
    val id: Long
) {
    val downloadTime = Date(System.currentTimeMillis())
}

data class CommentsSnapshot(
    val comments: List<Comment>
) {
    companion object {
        fun parse(objectMapper: ObjectMapper, json: String): CommentsSnapshot {
            val comments: MutableList<Comment> = arrayListOf()

            fun parseComment(jsonComment: JsonNode, parentId: Long?, depth: Int): Comment =
                with(jsonComment.get("data")) {
                    val curId = countComments++

                    val children: MutableList<Long> = arrayListOf()
                    val replies = get("replies")?.get("data")?.get("children")
                    if (replies != null) {
                        for (child in replies) {
                            children.add(parseComment(child, curId, depth + 1).id)
                        }
                    }

                    val comment = Comment(
                        creationTime = Date(get("created").asLong() * 1000),
                        ups = get("ups").asLong(),
                        downs = get("downs").asLong(),
                        text = get("body").toPrettyString(),
                        author = get("author").toString(),
                        replyTo = parentId,
                        replies = children,
                        depth = depth,
                        id = curId
                    )
                    comments.add(comment)
                    return comment
                }

            val jsonComments = objectMapper.readTree(json).get(1).get("data").get("children")
            for (jsonComment in jsonComments) {
                parseComment(jsonComment, null, 0)
            }
            comments.sortBy { it.id }
            return CommentsSnapshot(comments)


        }

    }
}