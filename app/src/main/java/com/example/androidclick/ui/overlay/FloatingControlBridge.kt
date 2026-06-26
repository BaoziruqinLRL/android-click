package com.example.androidclick.ui.overlay

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import com.example.androidclick.service.ClickForegroundService
import com.example.androidclick.service.ClickServiceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

object FloatingControlBridge {

    private var controlBar: FloatingControlBar? = null
    private var bridgeScope: CoroutineScope? = null
    private var collectionJob: Job? = null

    fun initialize(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        savedStateRegistryOwner: SavedStateRegistryOwner
    ) {
        if (controlBar != null) return

        val bar = FloatingControlBar(
            context = context.applicationContext,
            lifecycleOwner = lifecycleOwner,
            savedStateRegistryOwner = savedStateRegistryOwner
        ).apply {
            onStart = {
                // 用 startForegroundService 重启前台服务（Android 14+ 要求）
                val intent = Intent(
                    context.applicationContext,
                    ClickForegroundService::class.java
                ).apply {
                    action = ClickForegroundService.ACTION_START
                }
                context.applicationContext.startForegroundService(intent)
            }
            onPause = {
                ClickForegroundService.sendAction(context.applicationContext, ClickForegroundService.ACTION_PAUSE)
            }
            onResume = {
                ClickForegroundService.sendAction(context.applicationContext, ClickForegroundService.ACTION_RESUME)
            }
            onStop = {
                ClickForegroundService.sendAction(context.applicationContext, ClickForegroundService.ACTION_STOP)
            }
        }

        controlBar = bar

        // 创建新 scope 收集状态，不受 Activity 生命周期影响
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        bridgeScope = scope
        collectionJob = scope.launch {
            ClickForegroundService.state.collectLatest { state ->
                bar.updateState(state)
            }
        }
    }

    fun show() {
        controlBar?.show()
    }

    fun hide() {
        controlBar?.hide()
    }

    fun isShowing(): Boolean = controlBar?.isShowing() ?: false

    fun resetPosition() {
        controlBar?.resetPosition()
    }

    fun destroy() {
        collectionJob?.cancel()
        collectionJob = null
        bridgeScope?.cancel()
        bridgeScope = null
        controlBar?.hide()
        controlBar = null
    }
}
