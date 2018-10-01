package main

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

fun runSimpleCase() {
    val s = SomethingWithState()
    s.run()
}


abstract class OpDescriptor {
    abstract fun perform(affected: SomethingWithState): Any?
}

class SomethingWithState() {
    var _state: AtomicRef<Any?> = atomic(UnlockOp(Empty()))
    private class Empty()

    fun run() {
        (this._state.value as UnlockOp).perform(this)
    }

    private class UnlockOp(val queue: Empty): OpDescriptor() {
        override fun perform(affected: SomethingWithState): Any? {
            val update: Any = Empty()
            affected._state.compareAndSet(this@UnlockOp, update)
            return if (affected._state.value == Empty()) null else affected;
        }
    }
}