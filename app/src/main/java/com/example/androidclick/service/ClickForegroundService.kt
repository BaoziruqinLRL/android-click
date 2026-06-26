package com.example.androidclick.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.androidclick.MainActivity
import com.example.androidclick.R
import com.example.androidclick.domain.model.ClickState
import com.example.androidclick.util.ClickLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import android.content.pm.ServiceInfo
import kotlinx.coroutines.launch

class ClickForegroundService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var scheduler: ClickScheduler? = null
    private var connectionJob: Job? = null
    private var totalCount: Int = -1

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        connectionJob = serviceScope.launch {
            ClickAccessibilityService.connectionEvents.collect { event ->
                if (event == ClickAccessibilityService.ConnectionEvent.Disconnected) {
                    updateState {
                        it.copy(
                            state = ClickState.Stopped,
                            errorMessage = "无障碍服务已关闭"
                        )
                    }
                    stopClicking()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                // 如果 Intent 没有携带坐标（如悬浮窗发起的 start），复用缓存的参数
                if (intent.hasExtra(EXTRA_X) && intent.hasExtra(EXTRA_Y)) {
                    val x = intent.getFloatExtra(EXTRA_X, 0f)
                    val y = intent.getFloatExtra(EXTRA_Y, 0f)
                    val interval = intent.getLongExtra(EXTRA_INTERVAL, 500L)
                    val count = intent.getIntExtra(EXTRA_COUNT, -1)
                    startClicking(x, y, interval, count)
                } else {
                    startClicking(lastX, lastY, lastIntervalMs, lastCount)
                }
            }

            ACTION_STOP -> stopClicking()

            ACTION_PAUSE -> {
                scheduler?.pause()
                updateState { it.copy(state = ClickState.Paused) }
            }

            ACTION_RESUME -> {
                scheduler?.resume()
                updateState { it.copy(state = ClickState.Running) }
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        connectionJob?.cancel()
        scheduler?.cancel()
        scheduler = null
        super.onDestroy()
    }

    private fun startClicking(x: Float, y: Float, intervalMs: Long, count: Int) {
        if (!ClickAccessibilityService.isConnected) {
            updateState {
                ClickServiceState(
                    state = ClickState.Stopped,
                    errorMessage = "无障碍服务未开启，请先授权"
                )
            }
            stopSelf()
            return
        }

        // 缓存参数，供悬浮窗重新发起连点使用
        lastX = x
        lastY = y
        lastIntervalMs = intervalMs
        lastCount = count

        scheduler?.cancel()
        scheduler = null
        totalCount = count

        updateState {
            ClickServiceState(
                state = ClickState.Running,
                currentCount = 0,
                totalCount = count,
                errorMessage = null,
                failureCount = 0
            )
        }

        val notification = buildNotification(0, count)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_SPECIAL_USE_VALUE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        scheduler = ClickScheduler(
            x = x,
            y = y,
            intervalMs = intervalMs,
            repeatCount = count,
            tap = { performTap(x, y) },
            onTick = { current ->
                updateState {
                    it.copy(
                        state = ClickState.Running,
                        currentCount = current,
                        totalCount = totalCount
                    )
                }
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.notify(
                    NOTIFICATION_ID,
                    buildNotification(current, totalCount)
                )
            },
            onComplete = { stopClicking() },
            onError = { message ->
                updateState {
                    it.copy(
                        errorMessage = message,
                        failureCount = it.failureCount + 1
                    )
                }
            }
        ).also { it.start() }
    }

    private suspend fun performTap(x: Float, y: Float): Boolean {
        val service = ClickAccessibilityService.instance
        if (service == null) {
            ClickLog.e("Accessibility service unavailable during tap")
            return false
        }
        return service.tap(x, y)
    }

    private fun stopClicking() {
        scheduler?.cancel()
        scheduler = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        updateState {
            if (it.state == ClickState.Running || it.state == ClickState.Paused) {
                it.copy(state = ClickState.Stopped)
            } else {
                it.copy(state = ClickState.Idle)
            }
        }
        stopSelf()
    }

    private fun updateState(transform: (ClickServiceState) -> ClickServiceState) {
        _state.update(transform)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_description)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(currentCount: Int, total: Int): Notification {
        val openIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getService(
            this,
            1,
            Intent(this, ClickForegroundService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE
        )

        val countText = if (total < 0) {
            getString(R.string.notification_count_infinite, currentCount)
        } else {
            getString(R.string.notification_count_finite, currentCount, total)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(countText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(openIntent)
            .setOngoing(true)
            .addAction(0, getString(R.string.notification_action_stop), stopIntent)
            .build()
    }

    companion object {
        const val ACTION_START = "com.example.androidclick.action.START"
        const val ACTION_STOP = "com.example.androidclick.action.STOP"
        const val ACTION_PAUSE = "com.example.androidclick.action.PAUSE"
        const val ACTION_RESUME = "com.example.androidclick.action.RESUME"

        const val EXTRA_X = "extra_x"
        const val EXTRA_Y = "extra_y"
        const val EXTRA_INTERVAL = "extra_interval"
        const val EXTRA_COUNT = "extra_count"

        private const val CHANNEL_ID = "clicker_channel"
        private const val NOTIFICATION_ID = 1001
        private const val FOREGROUND_SERVICE_TYPE_SPECIAL_USE_VALUE = 1 shl 7 // 0x80

        private val _state = MutableStateFlow(ClickServiceState())
        val state: StateFlow<ClickServiceState> = _state.asStateFlow()

        // 缓存最近一次启动参数，供悬浮窗重新发起连点使用
        private var lastX: Float = 0f
        private var lastY: Float = 0f
        private var lastIntervalMs: Long = 500L
        private var lastCount: Int = -1

        fun start(
            context: Context,
            x: Float,
            y: Float,
            intervalMs: Long,
            count: Int
        ) {
            val intent = Intent(context, ClickForegroundService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_X, x)
                putExtra(EXTRA_Y, y)
                putExtra(EXTRA_INTERVAL, intervalMs)
                putExtra(EXTRA_COUNT, count)
            }
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, ClickForegroundService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        fun pause(context: Context) {
            val intent = Intent(context, ClickForegroundService::class.java).apply {
                action = ACTION_PAUSE
            }
            context.startService(intent)
        }

        fun resume(context: Context) {
            val intent = Intent(context, ClickForegroundService::class.java).apply {
                action = ACTION_RESUME
            }
            context.startService(intent)
        }

        fun sendAction(context: Context, action: String) {
            val intent = Intent(context, ClickForegroundService::class.java).apply {
                this.action = action
            }
            context.startService(intent)
        }
    }
}
