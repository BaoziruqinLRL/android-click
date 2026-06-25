package com.example.androidclick.domain.model

data class ClickPoint(
    val x: Float,
    val y: Float,
    val delayAfterMs: Long = 0
)
