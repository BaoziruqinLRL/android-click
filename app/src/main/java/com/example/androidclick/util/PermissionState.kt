package com.example.androidclick.util

data class PermissionState(
    val accessibility: Boolean = false,
    val overlay: Boolean = false,
    val notification: Boolean = false
) {
    val allGranted: Boolean
        get() = accessibility && overlay && notification
}
