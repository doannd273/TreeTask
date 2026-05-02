package com.doannd3.treetask.core.network.di

import com.doannd3.treetask.core.network.service.AuthService
import com.doannd3.treetask.core.network.service.ChatService
import com.doannd3.treetask.core.network.service.StatsService
import com.doannd3.treetask.core.network.service.TaskService
import com.doannd3.treetask.core.network.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {
    @Provides
    @Singleton
    fun provideAuthService(
        @AuthNetwork retrofit: Retrofit,
    ): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideTaskService(
        @AuthenticatedNetwork retrofit: Retrofit,
    ): TaskService {
        return retrofit.create(TaskService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(
        @AuthenticatedNetwork retrofit: Retrofit,
    ): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideStatsService(
        @AuthenticatedNetwork retrofit: Retrofit,
    ): StatsService {
        return retrofit.create(StatsService::class.java)
    }

    @Provides
    @Singleton
    fun provideChatService(
        @AuthenticatedNetwork retrofit: Retrofit,
    ): ChatService {
        return retrofit.create(ChatService::class.java)
    }
}
