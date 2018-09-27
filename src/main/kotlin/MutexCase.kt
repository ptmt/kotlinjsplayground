package main

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun runMutexCase() {
    launchWithKey("key1")
    launchWithKey("key2")
    launchWithKey("key3")
    launchWithKey("key1")
    launchWithKey("key2")
}

fun launchWithKey(key: String) {
    GlobalScope.launch {
        console.log("launch with $key")
        withSomeMutex(key)
    }
}

val mutex = Mutex()
val map = mutableMapOf<String, Int>()
val mutexPerKey = mutableMapOf<String, Mutex>()

suspend fun withSomeMutex(key: String): Int {
    val mutex = mutexPerKey.getOrPut(key) { Mutex() }
    console.log("$key - mutex ${mutex.isLocked}")
    mutex.withLock {
        return map.getOrPut(key) {
            expensiveAsyncIO(key)
        }
    }
}

suspend fun expensiveAsyncIO(key: String): Int {
    console.log("${key} - expensiveAsyncIO started")
    delay(300)
    if (key == "key3") {
        console.log("an operation under a mutex calls another")
        withSomeMutex("key1")
    }
    console.log("${key} - expensiveAsyncIO for finished")
    return 1
}
