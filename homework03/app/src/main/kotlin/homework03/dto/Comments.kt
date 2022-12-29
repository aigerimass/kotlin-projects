package homework03.dto

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.lang.NullPointerException
import java.util.*

var countComments: Long = 0

data class Comment(
    val id: Long,
    val creationTime: Date,
    val author: String,
    val depth: Int,
    val text: String,
    val ups: Long,
    val downs: Long,
    val replyTo: Long?,
    val replies: List<Long>,
    var postId: String = ""
) {
    val downloadTime = Date(System.currentTimeMillis())
}

data class CommentsSnapshot(
    val comments: List<Comment>
) {
    companion object {
        fun parse(objectMapper: ObjectMapper, json: String): CommentsSnapshot {
            val comments: MutableList<Comment> = arrayListOf()

            fun parseComment(jsonComment: JsonNode, parentId: Long?, depth: Int): Comment? =
                with(jsonComment.get("data")) {
                    val curId = countComments++

                    val children: MutableList<Long> = arrayListOf()
                    val replies = get("replies")?.get("data")?.get("children")
                    if (replies != null) {
                        for (child in replies) {
                            val childCom = parseComment(child, curId, depth + 1)
                            if (childCom != null) children.add(childCom.id)
                        }
                    }
                    // some comments are not parsable, just skip
                    try {
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
                    } catch (e: NullPointerException) {
                        return null
                    }
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