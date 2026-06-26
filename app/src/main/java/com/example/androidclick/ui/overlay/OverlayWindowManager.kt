package com.example.androidclick.ui.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager

class OverlayWindowManager(private val context: Context) {

    private val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private var overlayView: View? = null
    private var isShowing = false

    private val layoutType: Int
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

    fun canDrawOverlays(): Boolean = Settings.canDrawOverlays(context)

    fun show(view: View, x: Int = 0, y: Int = 200) {
        if (isShowing) return
        if (!canDrawOverlays()) return

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            this.x = x
            this.y = y
        }

        try {
            windowManager.addView(view, params)
            overlayView = view
            isShowing = true
        } catch (e: Exception) {
            // Permission denied or other window error
        }
    }

    fun hide() {
        overlayView?.let { view ->
            try {
                windowManager.removeView(view)
            } catch (_: IllegalArgumentException) {
                // View was already removed
            }
        }
        overlayView = null
        isShowing = false
    }

    fun updatePosition(x: Int, y: Int) {
        overlayView?.let { view ->
            val params = view.layoutParams as? WindowManager.LayoutParams ?: return
            params.x = x
            params.y = y
            try {
                windowManager.updateViewLayout(view, params)
            } catch (_: IllegalArgumentException) {
                // View not attached
            }
        }
    }

    fun isViewShowing(): Boolean = isShowing
}
