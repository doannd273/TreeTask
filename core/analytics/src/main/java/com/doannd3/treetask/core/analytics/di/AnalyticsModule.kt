package com.doannd3.treetask.core.analytics.di

import com.doannd3.treetask.core.analytics.AnalyticsHelper
import com.doannd3.treetask.core.analytics.FirebaseAnalyticsHelper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {
    @Binds
    abstract fun bindAnalyticsHelper(analyticsHelper: FirebaseAnalyticsHelper): AnalyticsHelper
}
