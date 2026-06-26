package com.example.androidclick.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClickScriptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ClickScriptEntity): Long

    @Update
    suspend fun update(entity: ClickScriptEntity)

    @Delete
    suspend fun delete(entity: ClickScriptEntity)

    @Query("SELECT * FROM click_scripts WHERE id = :id")
    suspend fun getById(id: Long): ClickScriptEntity?

    @Query("SELECT * FROM click_scripts ORDER BY updatedAt DESC")
    suspend fun getAll(): List<ClickScriptEntity>

    @Query("SELECT * FROM click_scripts ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<ClickScriptEntity>>
}
