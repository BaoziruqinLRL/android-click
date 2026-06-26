package com.example.androidclick.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ClickScriptEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ClickerConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clickScriptDao(): ClickScriptDao
}
