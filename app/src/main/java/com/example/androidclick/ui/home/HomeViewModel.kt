package com.example.androidclick.ui.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidclick.domain.model.ClickState
import com.example.androidclick.domain.usecase.ObserveClickStateUseCase
import com.example.androidclick.service.ClickForegroundService
import com.example.androidclick.service.ClickScheduler
import com.example.androidclick.service.ClickServiceState
import com.example.androidclick.util.PermissionChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ClickDebugForm(
    val x: String = "540",
    val y: String = "1200",
    val intervalMs: String = "500",
    val repeatCount: String = "20"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeClickState: ObserveClickStateUseCase
) : ViewModel() {

    val clickServiceState: StateFlow<ClickServiceState> = observeClickState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ClickServiceState()
        )

    var form by mutableStateOf(ClickDebugForm())
        private set

    var isAccessibilityEnabled by mutableStateOf(false)
        private set

    val isClicking: Boolean
        get() = clickServiceState.value.state == ClickState.Running ||
            clickServiceState.value.state == ClickState.Paused

    fun refreshAccessibility(context: Context) {
        isAccessibilityEnabled = PermissionChecker.isAccessibilityServiceEnabled(context)
    }

    fun updateX(value: String) {
        form = form.copy(x = value)
    }

    fun updateY(value: String) {
        form = form.copy(y = value)
    }

    fun updateInterval(value: String) {
        form = form.copy(intervalMs = value)
    }

    fun updateRepeatCount(value: String) {
        form = form.copy(repeatCount = value)
    }

    fun openAccessibilitySettings(context: Context) {
        PermissionChecker.openAccessibilitySettings(context)
    }

    fun startClicking(context: Context): String? {
        if (!isAccessibilityEnabled) {
            return "请先开启无障碍服务"
        }

        val x = form.x.toFloatOrNull()
            ?: return "X 坐标格式不正确"
        val y = form.y.toFloatOrNull()
            ?: return "Y 坐标格式不正确"
        val interval = form.intervalMs.toLongOrNull()
            ?: return "间隔格式不正确"
        val count = form.repeatCount.toIntOrNull()
            ?: return "次数格式不正确"

        if (interval < ClickScheduler.MIN_INTERVAL_MS) {
            return "间隔不能小于 ${ClickScheduler.MIN_INTERVAL_MS}ms"
        }

        ClickForegroundService.start(
            context = context.applicationContext,
            x = x,
            y = y,
            intervalMs = interval,
            count = count
        )
        return null
    }

    fun stopClicking(context: Context) {
        ClickForegroundService.stop(context.applicationContext)
    }
}
