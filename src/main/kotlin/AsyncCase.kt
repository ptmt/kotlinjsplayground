package AsyncCase

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

fun runAwaitAll() {
    val job = GlobalScope.launch {

        val tasks = listOf(1, 2).map {
            async { asyncIO(it) }
        }
        val finishedTasks = tasks.awaitAll()
        console.log("awaited all $finishedTasks")
    }
    job.invokeOnCompletion {
        console.log("job finished, exception", it)
    }
}

suspend fun asyncIO(i: Int): Int {
    val timeToDelay = Random.nextInt(300) + 100
    delay(timeToDelay)
    console.log("delayed $i for ")
    return i
}