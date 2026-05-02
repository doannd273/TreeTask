package com.doannd3.treetask.core.database.di

import android.content.Context
import androidx.room.Room
import com.doannd3.treetask.core.database.TreeTaskDatabase
import com.doannd3.treetask.core.database.dao.TaskDao
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
    fun providesDatabase(
        @ApplicationContext context: Context,
    ): TreeTaskDatabase = Room.databaseBuilder(
        context,
        TreeTaskDatabase::class.java,
        "tree-task-database",
    ).build()

    @Provides
    fun provideTaskDao(database: TreeTaskDatabase): TaskDao {
        return database.taskDao()
    }
}
