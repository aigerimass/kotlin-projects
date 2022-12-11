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
}

fun main() {
    println(App().greeting)
    App().dummy_start()
}
