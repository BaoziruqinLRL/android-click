package com.example.androidclick.domain.model

data class ClickScript(
    val id: Long = 0,
    val name: String,
    val clickMode: ClickMode,
    val intervalMs: Long,
    val intervalRandom: Boolean,
    val intervalMaxMs: Long?,
    val repeatCount: Int,
    val points: List<ClickPoint>,
    val createdAt: Long,
    val updatedAt: Long
)
