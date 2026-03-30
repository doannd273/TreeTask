package com.doannd3.treetask.core.data.di

import com.doannd3.treetask.core.data.respository.AuthRepositoryImpl
import com.doannd3.treetask.core.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Đánh dấu đây là Trạm Cấp Phát Vũ Khí của Hilt
@Module
// InstallIn báo cho Hilt biết: Cục Repo này là Singleton (sống cùng tuổi đời của toàn App)
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    // Kỹ thuật gán (Binds) thay thế chữ "Provides" giúp tối ưu hiệu suất bộ nhớ cực mạnh
    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl // Đứa con ruột (Data)
    ): AuthRepository                            // Interface màng bọc (Domain)

}