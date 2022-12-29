package homework03

import com.soywiz.korio.async.async
import com.soywiz.korio.async.launch
import com.soywiz.korio.file.VfsOpenMode
import com.soywiz.korio.file.std.localVfs
import com.soywiz.korio.stream.writeString
import homework03.csv.csvSerialize
import homework03.dto.CommentWrapper
import homework03.dto.PostWrapper
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

private suspend fun removePrevFile(path: String, filename: String) {
    val fileVfs = localVfs(path)
    fileVfs[filename].delete()
}

private suspend fun writeTopicComments(path: String, topicName: String) {
    val topic = RedditClient.getTopic(topicName)
    val posts = topic.posts.map { PostWrapper(topicName, it) }
    val fileVfs = localVfs(path)
    val topicFile = fileVfs["--subjects.csv"].open(VfsOpenMode.WRITE)
    val commentsFile = fileVfs["--comments.csv"].open(VfsOpenMode.WRITE)
    try {
        coroutineScope {
            launch {
                topicFile.writeString(csvSerialize(posts, PostWrapper::class))
            }
            for (post in posts) {
                launch {
                    val comments = RedditClient.getComments(topicName, post.id).comments
                        .map { CommentWrapper(topicName, post.id, it) }
                    commentsFile.writeString(csvSerialize(comments, CommentWrapper::class))
                }
            }
        }
    } finally {
        topicFile.close()
        commentsFile.close()
    }
}

fun main(args: Array<String>) {
    if (args.size < 2) throw IllegalArgumentException("arg0: path, other args: topics names")
    val path = args[0]
    val topics = args.asList().subList(1, args.size)
    runBlocking {
        removePrevFile(path, "--subjects.csv")
        removePrevFile(path, "--comments.csv")
        topics.forEach { async { writeTopicComments(path, it) } }
    }
}
