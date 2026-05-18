package com.doannd3.treetask.core.network.di

import com.doannd3.treetask.core.network.header.AcceptLanguageProvider
import com.doannd3.treetask.core.network.header.DefaultAcceptLanguageProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NetworkHeaderModule {
    @Binds
    @Singleton
    fun bindAcceptLanguageProvider(
        impl: DefaultAcceptLanguageProvider,
    ): AcceptLanguageProvider
}
