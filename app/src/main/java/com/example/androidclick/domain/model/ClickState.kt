package com.example.androidclick.domain.model

sealed class ClickState {
    data object Idle : ClickState()
    data object Running : ClickState()
    data object Paused : ClickState()
    data object Stopped : ClickState()
}
