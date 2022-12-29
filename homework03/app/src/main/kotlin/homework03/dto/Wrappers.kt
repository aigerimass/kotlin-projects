package homework03.dto

import java.util.*

data class PostWrapper(
    val topicId: String,
    val id: String,
    val author: String,
    val createdEpochTime: Double,
    val upVotes: Long,
    val downVotes: Long,
    val title: String,
    val selftext: String?,
    val selfhtmlText: String?
) {
    constructor(topicId: String, post: Post) : this(topicId,
        post.id,
        post.author,
        post.createdEpochTime,
        post.upVotes,
        post.downVotes,
        post.title,
        post.selftext,
        post.selfhtmlText)
}

data class CommentWrapper(
    val topicId: String,
    val postId: String,
    val id: Long,
    val creationTime: Date,
    val author: String,
    val depth: Int,
    val text: String,
    val ups: Long,
    val downs: Long,
    val replyTo: Long?,
    val replies: List<Long>,
    val downloadTime: Date
) {
    constructor(topicId: String, postId: String, comment: Comment) : this(
        topicId,
        postId,
        comment.id,
        comment.creationTime,
        comment.author,
        comment.depth,
        comment.text,
        comment.ups,
        comment.downs,
        comment.replyTo,
        comment.replies,
        comment.downloadTime
    )
}

