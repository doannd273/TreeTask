package com.doannd3.treetask.core.data.di

import com.doannd3.treetask.core.common.network.NetworkMonitor
import com.doannd3.treetask.core.data.respository.AuthRepositoryImpl
import com.doannd3.treetask.core.data.respository.TaskRepositoryImpl
import com.doannd3.treetask.core.data.respository.UserRepositoryImpl
import com.doannd3.treetask.core.data.util.ConnectivityManagerNetworkMonitor
import com.doannd3.treetask.core.domain.repository.AuthRepository
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
    fun bindNetworkMonitor(networkMonitor: ConnectivityManagerNetworkMonitor): NetworkMonitor
}
