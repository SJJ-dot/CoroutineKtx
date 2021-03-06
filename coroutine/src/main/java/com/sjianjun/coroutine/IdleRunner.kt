package com.sjianjun.coroutine

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private val idleHandler by lazy { Handler(Looper.getMainLooper()) }

fun runOnIdle(runner: () -> Unit) {
    idleHandler.post {
        Looper.myQueue().addIdleHandler {
            runner()
            false
        }
    }
}

suspend inline fun <T> withIdle(crossinline runner: suspend () -> T) {
    suspendCancellableCoroutine<T> {
        runOnIdle {
            runBlocking {
                try {
                    it.resume(runner())
                } catch (e: Throwable) {
                    it.resumeWithException(e)
                }
            }
        }
    }
}