package com.example.androidclick.service

import com.example.androidclick.domain.model.ClickState

data class ClickServiceState(
    val state: ClickState = ClickState.Idle,
    val currentCount: Int = 0,
    val totalCount: Int = -1,
    val errorMessage: String? = null,
    val failureCount: Int = 0
)
