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
        val comments = RedditClient.getComments(
            "https://www.reddit.com/r/Kotlin/comments/z02i23/what_is_dispatchersdefaults_maximum_number_of/.json")
        println(comments)
        val comments2 = RedditClient.getComments("Kotlin", "z3qwxa/additional_monads_not_defined_in_arrow")
        println(comments2)
    }
}

fun main() {
    println(App().greeting)
//    App().dummy_start()
    App().dummy_comments()
}
