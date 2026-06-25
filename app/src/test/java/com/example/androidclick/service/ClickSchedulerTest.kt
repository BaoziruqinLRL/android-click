package com.example.androidclick.service

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class ClickSchedulerTest {

    @Test
    fun `rejects interval below minimum`() {
        var errorMessage: String? = null
        val scheduler = createScheduler(
            intervalMs = 10,
            repeatCount = 5,
            onError = { errorMessage = it }
        )
        scheduler.start()
        assertEquals("点击间隔不能小于 ${ClickScheduler.MIN_INTERVAL_MS}ms", errorMessage)
        assertFalse(scheduler.isRunning)
    }

    @Test
    fun `runs exact number of taps`() = runBlocking {
        val tapCount = AtomicInteger(0)
        val completed = AtomicBoolean(false)

        val scheduler = createScheduler(
            intervalMs = 50,
            repeatCount = 10,
            tap = {
                tapCount.incrementAndGet()
                true
            },
            onComplete = { completed.set(true) }
        )

        scheduler.start()
        delay(1_500)

        assertTrue(tapCount.get() >= 10)
        scheduler.stop()
        assertTrue(completed.get())
    }

    @Test
    fun `pause and resume continues clicking`() = runBlocking {
        val tapCount = AtomicInteger(0)

        val scheduler = createScheduler(
            intervalMs = 50,
            repeatCount = -1,
            tap = {
                tapCount.incrementAndGet()
                true
            }
        )

        scheduler.start()
        delay(200)
        scheduler.pause()
        val pausedCount = tapCount.get()
        delay(300)
        assertEquals(pausedCount, tapCount.get())

        scheduler.resume()
        delay(300)
        assertTrue(tapCount.get() > pausedCount)
        scheduler.stop()
    }

    private fun createScheduler(
        intervalMs: Long = 50,
        repeatCount: Int = 10,
        tap: suspend () -> Boolean = { true },
        onTick: (Int) -> Unit = {},
        onComplete: () -> Unit = {},
        onError: (String) -> Unit = {}
    ): ClickScheduler {
        return ClickScheduler(
            x = 100f,
            y = 200f,
            intervalMs = intervalMs,
            repeatCount = repeatCount,
            tap = tap,
            onTick = onTick,
            onComplete = onComplete,
            onError = onError
        )
    }
}
