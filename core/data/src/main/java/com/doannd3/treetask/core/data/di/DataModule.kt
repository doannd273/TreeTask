package com.doannd3.treetask.core.data.di

import com.doannd3.treetask.core.common.network.NetworkMonitor
import com.doannd3.treetask.core.data.repository.AuthRepositoryImpl
import com.doannd3.treetask.core.data.repository.ChatRealtimeRepositoryImpl
import com.doannd3.treetask.core.data.repository.ChatRepositoryImpl
import com.doannd3.treetask.core.data.repository.DeviceRepositoryImpl
import com.doannd3.treetask.core.data.repository.SessionRepositoryImpl
import com.doannd3.treetask.core.data.repository.SettingRepositoryImpl
import com.doannd3.treetask.core.data.repository.StatsRepositoryImpl
import com.doannd3.treetask.core.data.repository.TaskRepositoryImpl
import com.doannd3.treetask.core.data.repository.UserRepositoryImpl
import com.doannd3.treetask.core.data.util.ConnectivityManagerNetworkMonitor
import com.doannd3.treetask.core.domain.repository.AuthRepository
import com.doannd3.treetask.core.domain.repository.ChatRealtimeRepository
import com.doannd3.treetask.core.domain.repository.ChatRepository
import com.doannd3.treetask.core.domain.repository.DeviceRepository
import com.doannd3.treetask.core.domain.repository.SessionRepository
import com.doannd3.treetask.core.domain.repository.SettingRepository
import com.doannd3.treetask.core.domain.repository.StatsRepository
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.doannd3.treetask.core.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    fun bindTasksRepository(taskRepositoryImpl: TaskRepositoryImpl): TaskRepository

    @Binds
    fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    fun bindSettingRepository(settingRepositoryImpl: SettingRepositoryImpl): SettingRepository

    @Binds
    fun bindSessionRepository(sessionRepositoryImpl: SessionRepositoryImpl): SessionRepository

    @Binds
    fun bindDeviceRepository(deviceRepositoryImpl: DeviceRepositoryImpl): DeviceRepository

    @Binds
    fun bindStatsRepository(statsRepositoryImpl: StatsRepositoryImpl): StatsRepository

    @Binds
    fun bindChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository

    @Binds
    fun bindNetworkMonitor(networkMonitor: ConnectivityManagerNetworkMonitor): NetworkMonitor

    @Binds
    fun bindChatRealtimeRepository(chatRealtimeRepositoryImpl: ChatRealtimeRepositoryImpl): ChatRealtimeRepository
}
