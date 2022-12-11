package homework03

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
        val comments = RedditClient.getComments("z3qwxa/additional_monads_not_defined_in_arrow")
        println(comments)
    }
}

fun main() {
    println(App().greeting)
//    App().dummy_start()
    App().dummy_comments()
}
