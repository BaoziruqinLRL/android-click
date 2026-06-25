package com.example.androidclick.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.example.androidclick.util.ClickLog
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ClickAccessibilityService : AccessibilityService() {

    private val gestureDispatcher by lazy { GestureDispatcher(this) }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        ClickLog.d("Accessibility service connected")
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        instance = null
        _connectionEvents.tryEmit(ConnectionEvent.Disconnected)
        ClickLog.w("Accessibility service disconnected")
        return super.onUnbind(intent)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 连点器不需要处理无障碍事件，仅注入手势
    }

    override fun onInterrupt() {
        ClickLog.w("Accessibility service interrupted")
    }

    suspend fun tap(
        x: Float,
        y: Float,
        durationMs: Long = GestureDispatcher.DEFAULT_TAP_DURATION_MS
    ): Boolean = gestureDispatcher.dispatchTap(x, y, durationMs)

    enum class ConnectionEvent {
        Disconnected
    }

    companion object {
        @Volatile
        var instance: ClickAccessibilityService? = null
            private set

        val isConnected: Boolean
            get() = instance != null

        private val _connectionEvents = MutableSharedFlow<ConnectionEvent>(extraBufferCapacity = 1)
        val connectionEvents: SharedFlow<ConnectionEvent> = _connectionEvents.asSharedFlow()
    }
}
