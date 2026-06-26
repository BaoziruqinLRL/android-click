package com.example.androidclick.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidclick.domain.model.ClickMode
import com.example.androidclick.domain.model.ClickPoint

@Entity(tableName = "click_scripts")
data class ClickScriptEntity(
    @PrimaryKey(autoGenerate = true)
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
