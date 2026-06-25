package com.example.androidclick.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import com.example.androidclick.util.ClickLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class GestureDispatcher(
    private val service: AccessibilityService
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    suspend fun dispatchTap(
        x: Float,
        y: Float,
        durationMs: Long = DEFAULT_TAP_DURATION_MS
    ): Boolean {
        repeat(MAX_RETRIES + 1) { attempt ->
            val success = dispatchTapOnce(x, y, durationMs)
            if (success) return true
            if (attempt < MAX_RETRIES) {
                ClickLog.w("dispatchTap retry ${attempt + 1}/$MAX_RETRIES at ($x, $y)")
                delay(RETRY_DELAY_MS)
            }
        }
        ClickLog.e("dispatchTap failed after ${MAX_RETRIES + 1} attempts at ($x, $y)")
        return false
    }

    private suspend fun dispatchTapOnce(
        x: Float,
        y: Float,
        durationMs: Long
    ): Boolean = suspendCancellableCoroutine { continuation ->
        val path = Path().apply { moveTo(x, y) }
        val stroke = GestureDescription.StrokeDescription(path, 0, durationMs)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()

        val dispatched = service.dispatchGesture(
            gesture,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    if (continuation.isActive) continuation.resume(true)
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    if (continuation.isActive) continuation.resume(false)
                }
            },
            mainHandler
        )

        if (!dispatched) {
            ClickLog.e("dispatchGesture returned false at ($x, $y)")
            if (continuation.isActive) continuation.resume(false)
        }
    }

    companion object {
        const val DEFAULT_TAP_DURATION_MS = 50L
        private const val MAX_RETRIES = 2
        private const val RETRY_DELAY_MS = 50L
    }
}
