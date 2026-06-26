package com.example.androidclick.di

import android.content.Context
import androidx.room.Room
import com.example.androidclick.data.local.AppDatabase
import com.example.androidclick.data.local.ClickScriptDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "clicker.db"
        ).build()
    }

    @Provides
    fun provideClickScriptDao(database: AppDatabase): ClickScriptDao {
        return database.clickScriptDao()
    }
}
