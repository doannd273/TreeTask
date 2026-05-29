package com.treestudio.treetask.di

import com.doannd3.treetask.core.domain.repository.PushTokenProvider
import com.treestudio.treetask.notification.FirebasePushTokenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface PushNotificationModule {
    @Binds
    fun bindPushTokenProvider(impl: FirebasePushTokenProvider): PushTokenProvider
}
