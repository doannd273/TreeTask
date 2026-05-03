package com.treestudio.treetask.di

import com.treestudio.treetask.initializer.AnalyticsSessionManager
import com.treestudio.treetask.initializer.AppInitializer
import com.treestudio.treetask.initializer.TimberInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
interface InitializerModule {
    @Binds
    @IntoSet
    fun bindTimberInitializer(initializer: TimberInitializer): AppInitializer

    @Binds
    @IntoSet
    fun bindAnalyticsSessionManager(manager: AnalyticsSessionManager): AppInitializer
}
