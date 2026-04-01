package com.treestudio.treetask.di

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
    @IntoSet // Bùa chú gom vào 1 List Set
    fun bindTimberInitializer(initializer: TimberInitializer): AppInitializer
}