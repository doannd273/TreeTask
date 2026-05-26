package com.treestudio.treetask.di

import com.doannd3.treetask.core.network.header.AcceptLanguageProvider
import com.treestudio.treetask.locale.AppLocaleAcceptLanguageProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppNetworkHeaderModule {
    @Singleton
    @Binds
    fun bindAcceptLanguageProvider(impl: AppLocaleAcceptLanguageProvider): AcceptLanguageProvider
}
