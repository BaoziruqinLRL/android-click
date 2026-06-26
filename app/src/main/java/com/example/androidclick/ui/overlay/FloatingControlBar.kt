package com.example.androidclick.ui.overlay

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.MotionEvent
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.androidclick.R
import com.example.androidclick.domain.model.ClickState
import com.example.androidclick.service.ClickServiceState
import kotlin.math.roundToInt

class FloatingControlBar(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val savedStateRegistryOwner: SavedStateRegistryOwner
) {

    private val overlayManager = OverlayWindowManager(context)
    private var composeView: ComposeView? = null

    var state: ClickServiceState by mutableStateOf(ClickServiceState())
    var onStart: (() -> Unit)? = null
    var onPause: (() -> Unit)? = null
    var onResume: (() -> Unit)? = null
    var onStop: (() -> Unit)? = null

    private var initialX = 0
    private var initialY = 0
    private var dragStartX = 0f
    private var dragStartY = 0f
    private var isDragging = false

    fun show() {
        if (overlayManager.isViewShowing()) return
        if (!overlayManager.canDrawOverlays()) return

        // applicationContext 无主题，ComposeView 需带主题的 Context
        val themedContext = ContextThemeWrapper(context, R.style.Theme_AndroidClick)
        val view = ComposeView(themedContext).also { cv ->
            cv.setViewTreeLifecycleOwner(lifecycleOwner)
            cv.setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
            cv.setContent {
                FloatingBarContent(
                    state = state,
                    onStart = { onStart?.invoke() },
                    onPause = { onPause?.invoke() },
                    onResume = { onResume?.invoke() },
                    onStop = { onStop?.invoke() }
                )
            }
        }

        // Drag support via OnTouchListener
        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = (view.layoutParams as? WindowManager.LayoutParams)?.x ?: 0
                    initialY = (view.layoutParams as? WindowManager.LayoutParams)?.y ?: 0
                    dragStartX = event.rawX
                    dragStartY = event.rawY
                    isDragging = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - dragStartX
                    val dy = event.rawY - dragStartY
                    if (kotlin.math.abs(dx) > 10f || kotlin.math.abs(dy) > 10f) {
                        isDragging = true
                    }
                    if (isDragging) {
                        val newX = initialX + dx.roundToInt()
                        val newY = initialY + dy.roundToInt()
                        overlayManager.updatePosition(newX, newY)
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    if (isDragging) {
                        // Edge snapping
                        val displayMetrics = context.resources.displayMetrics
                        val screenWidth = displayMetrics.widthPixels
                        val currentX = (view.layoutParams as? WindowManager.LayoutParams)?.x ?: 0
                        val currentY = (view.layoutParams as? WindowManager.LayoutParams)?.y ?: 0

                        val snappedX = if (currentX < screenWidth / 2) {
                            0
                        } else {
                            screenWidth - view.width
                        }
                        overlayManager.updatePosition(snappedX, currentY)
                    }
                    true
                }

                else -> false
            }
        }

        composeView = view
        overlayManager.show(view)
    }

    fun hide() {
        overlayManager.hide()
        composeView = null
    }

    fun isShowing(): Boolean = overlayManager.isViewShowing()

    fun resetPosition() {
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        overlayManager.updatePosition(screenWidth, 200)
    }

    fun updateState(newState: ClickServiceState) {
        state = newState
    }
}

@Composable
private fun FloatingBarContent(
    state: ClickServiceState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    val isRunning = state.state == ClickState.Running
    val isPaused = state.state == ClickState.Paused

    val statusColor = when (state.state) {
        ClickState.Running -> Color(0xFF4CAF50)
        ClickState.Paused -> Color(0xFFFFC107)
        else -> Color(0xFF9E9E9E)
    }

    val countText = if (state.totalCount < 0) {
        "${state.currentCount} / ∞"
    } else {
        "${state.currentCount} / ${state.totalCount}"
    }

    Box(
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF2D2D2D))
            .border(1.dp, Color(0xFF555555), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Status indicator dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )

            // Click count
            Text(
                text = countText,
                color = Color.White,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )

            // Control buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isRunning) {
                    // Pause button
                    IconButton(
                        onClick = onPause,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Pause,
                            contentDescription = "暂停",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else if (isPaused) {
                    // Resume button
                    IconButton(
                        onClick = onResume,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "继续",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    // Start button (idle/stopped)
                    IconButton(
                        onClick = onStart,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "开始",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Stop button — always visible when running or paused
                if (isRunning || isPaused) {
                    IconButton(
                        onClick = onStop,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Stop,
                            contentDescription = "停止",
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
