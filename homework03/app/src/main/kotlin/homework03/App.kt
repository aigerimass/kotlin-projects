package homework03

import com.soywiz.korio.async.async
import com.soywiz.korio.file.VfsOpenMode
import com.soywiz.korio.file.std.localVfs
import com.soywiz.korio.stream.writeString
import homework03.csv.csvSerialize
import homework03.dto.Comment
import homework03.dto.Post
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking


class App {
    // get all comments from recent threads in topic
    suspend fun getRecentThreads(path: String, topics: List<String>) = runBlocking {
        data class PostWrapper(val idTopic: String, val idPost: String, val post: Post)
        val postJobs = arrayListOf<Deferred<List<PostWrapper>>>()
        topics.map { topic ->
            postJobs.add(async { RedditClient.getTopic(topic).posts.map {PostWrapper(topic, it.id, it)} })
        }
        val posts = postJobs.awaitAll().flatten()
        val postsCSV = csvSerialize(posts, PostWrapper::class)
        writeToCSV(postsCSV, path, "--subjects.csv")

        // got all posts, let's take comments from them
        data class CommentWrapper(val a_id: String, val comment: Comment)

        val commentJobs = arrayListOf<Deferred<List<CommentWrapper>>>()
        posts.map { post ->
            commentJobs.add(async {
                RedditClient.getComments(post.idTopic, post.idPost).comments.map {
                    CommentWrapper(post.idPost, it)
                }
            })
        }

        val commentsCsv = csvSerialize(commentJobs.awaitAll().flatten(), CommentWrapper::class)
        writeToCSV(commentsCsv, path, "--comments.csv")

    }

    // inside every topic we get comments to exact thread
    suspend fun getThread(path: String, topics: List<String>, threadIds: List<String>) = runBlocking {
        val topicJobs = arrayListOf<Deferred<List<Post>>>()
        val commentJobs = arrayListOf<Deferred<List<Comment>>>()
        for (i in topics.indices) {
            topicJobs.add(async { RedditClient.getTopic(topics[i]).posts })
            commentJobs.add(async { RedditClient.getComments(topics[i], threadIds[i]).comments })
        }
        val topicsCsv = csvSerialize(topicJobs.awaitAll().flatten(), Post::class)
        writeToCSV(topicsCsv, path, "--subjects.csv")

        val commentsCsv = csvSerialize(commentJobs.awaitAll().flatten(), Comment::class)
        writeToCSV(commentsCsv, path, "--comments.csv")
    }

    private suspend fun writeToCSV(csv: String, path: String, filename: String) {
        val fileVfs = localVfs(path)
        fileVfs[filename].delete()
        val file = fileVfs[filename].open(VfsOpenMode.CREATE)
        file.writeString(csv)
        file.close()
    }
}

fun main(args: Array<String>) = runBlocking {
    if (args.size < 2) throw IllegalArgumentException("arg0: path, other args: topics names")
    App().getRecentThreads(args[0], args.asList().subList(1, args.size))
}
