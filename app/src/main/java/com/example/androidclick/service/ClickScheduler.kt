package com.example.androidclick.service

import com.example.androidclick.util.ClickLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import java.util.concurrent.atomic.AtomicBoolean

class ClickScheduler(
    private val x: Float,
    private val y: Float,
    private val intervalMs: Long,
    private val repeatCount: Int,
    private val tap: suspend () -> Boolean,
    private val onTick: (currentCount: Int) -> Unit,
    private val onComplete: () -> Unit,
    private val onError: (message: String) -> Unit
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var job: Job? = null
    private val paused = AtomicBoolean(false)
    private val finished = AtomicBoolean(false)
    private var currentCount = 0
    private var consecutiveFailures = 0

    val isRunning: Boolean
        get() = job?.isActive == true

    fun start() {
        if (intervalMs < MIN_INTERVAL_MS) {
            onError("点击间隔不能小于 ${MIN_INTERVAL_MS}ms")
            return
        }
        if (job?.isActive == true) return

        currentCount = 0
        consecutiveFailures = 0
        paused.set(false)
        finished.set(false)

        job = scope.launch {
            ClickLog.d("ClickScheduler started at ($x, $y), interval=$intervalMs, count=$repeatCount")
            try {
                runLoop()
            } finally {
                finish()
            }
        }
    }

    fun pause() {
        paused.set(true)
        ClickLog.d("ClickScheduler paused")
    }

    fun resume() {
        paused.set(false)
        ClickLog.d("ClickScheduler resumed")
    }

    fun stop() {
        cancel()
        finish()
    }

    fun cancel() {
        job?.cancel()
        job = null
        paused.set(false)
    }

    private suspend fun runLoop() {
        while (coroutineContext.isActive) {
            waitIfPaused()
            if (!coroutineContext.isActive) break
            if (isFinished()) break

            val success = tap()
            if (success) {
                consecutiveFailures = 0
                currentCount++
                onTick(currentCount)
                ClickLog.d("Click #$currentCount at ($x, $y)")
            } else {
                consecutiveFailures++
                ClickLog.w("Click failed, consecutive failures=$consecutiveFailures")
                if (consecutiveFailures >= MAX_CONSECUTIVE_FAILURES) {
                    onError("连续点击失败 ${MAX_CONSECUTIVE_FAILURES} 次，已自动停止")
                    break
                }
            }

            if (isFinished()) break

            waitIfPaused()
            if (!coroutineContext.isActive) break
            delay(intervalMs)
        }
    }

    private suspend fun waitIfPaused() {
        while (paused.get() && coroutineContext.isActive) {
            delay(PAUSE_POLL_MS)
        }
    }

    private fun isFinished(): Boolean {
        return repeatCount >= 0 && currentCount >= repeatCount
    }

    private fun finish() {
        if (finished.compareAndSet(false, true)) {
            job = null
            paused.set(false)
            ClickLog.d("ClickScheduler finished, total clicks=$currentCount")
            onComplete()
        }
    }

    companion object {
        const val MIN_INTERVAL_MS = 50L
        private const val PAUSE_POLL_MS = 100L
        private const val MAX_CONSECUTIVE_FAILURES = 5
    }
}
