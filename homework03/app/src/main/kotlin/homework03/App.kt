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
    val greeting: String
        get() {
            return "Hello World!"
        }

    fun dummy_start() = runBlocking {
        val topic = RedditClient.getTopic("Kotlin")
        println(topic)
    }

    fun dummy_comments() = runBlocking {
        val comments = RedditClient.getComments(
            "https://www.reddit.com/r/Kotlin/comments/z02i23/what_is_dispatchersdefaults_maximum_number_of/.json"
        )
        println(comments)
        val comments2 = RedditClient.getComments("Kotlin", "z3qwxa")
        println(comments2)
    }

    private suspend fun writeToCSV(csv: String, path: String, filename: String) {
        val fileVfs = localVfs(path)
        fileVfs[filename].delete()
        val file = fileVfs[filename].open(VfsOpenMode.CREATE)
        file.writeString(csv)
        file.close()
    }

    // inside every topic we get comments to exact thread
    suspend fun getInfoFromThreads(path: String, topics: List<String>, threadIds: List<String>) = runBlocking {
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
}


fun main(args: Array<String>) = runBlocking {
    if (args.size % 2 != 1 || args.size < 3) {
        throw IllegalArgumentException("Correct args: path, topic name[1], thread name[1], ...")
    }
    val path = args[0]
    val topics = args.filterIndexed { i, _ -> i % 2 == 1 }
    val threads = args.filterIndexed { i, _ -> i % 2 == 0 && i > 0 }
    App().getInfoFromThreads(path, topics, threads)
}
