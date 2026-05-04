package com.doannd3.treetask.core.analytics.di

import android.content.Context
import com.doannd3.treetask.core.analytics.AnalyticsHelper
import com.doannd3.treetask.core.analytics.FirebaseAnalyticsHelper
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AnalyticsModule {
    @Binds
    fun bindAnalyticsHelper(analyticsHelper: FirebaseAnalyticsHelper): AnalyticsHelper

    companion object {
        @Provides
        @Singleton
        @android.annotation.SuppressLint("MissingPermission")
        fun provideFirebaseAnalytics(
            @ApplicationContext context: Context,
        ): FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

        @Provides
        @Singleton
        fun provideFirebaseCrashlytics(
            @ApplicationContext context: Context,
        ): FirebaseCrashlytics = FirebaseCrashlytics.getInstance()
    }
}
