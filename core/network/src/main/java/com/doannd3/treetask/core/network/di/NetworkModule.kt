package com.doannd3.treetask.core.network.di

import android.content.Context
import com.doannd3.treetask.core.network.BuildConfig
import com.doannd3.treetask.core.network.extensions.addChuckerInterceptors
import com.doannd3.treetask.core.network.extensions.addLoggingInterceptors
import com.doannd3.treetask.core.network.extensions.addNetworkDebugInterceptors
import com.doannd3.treetask.core.network.extensions.addTimeout
import com.doannd3.treetask.core.network.interceptor.AuthAuthenticator
import com.doannd3.treetask.core.network.interceptor.AuthInterceptor
import com.doannd3.treetask.core.network.interceptor.CommonHeaderInterceptor
import com.doannd3.treetask.core.network.util.ApiResultCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthNetwork

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedNetwork

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            // Bỏ qua key lạ nếu backend trả về thừa field (giúp app không bị crash)
            ignoreUnknownKeys = true
            // Nếu backend trả về "null" cho field không nullable, ném exception (thường dùng false tùy logic)
            coerceInputValues = true
            // Format date, naming strategy (nếu cần)
            // encodeDefaults = true
        }
    }

    // 1. Mạng DÀNH RIÊNG cho Auth (Không kẹp Token)
    @Provides
    @Singleton
    @AuthNetwork
    fun provideAuthOkHttpClient(
        @ApplicationContext context: Context,
        commonHeaderInterceptor: CommonHeaderInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addTimeout()
            .retryOnConnectionFailure(true)
            .addInterceptor(commonHeaderInterceptor)
            .addNetworkDebugInterceptors()
            .addChuckerInterceptors(context = context)
            .addLoggingInterceptors().build()
    }

    @Provides
    @Singleton
    @AuthenticatedNetwork
    fun provideAuthenticatedOkHttpClient(
        @ApplicationContext context: Context,
        commonHeaderInterceptor: CommonHeaderInterceptor,
        authInterceptor: AuthInterceptor,
        authAuthenticator: AuthAuthenticator,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addTimeout()
            .retryOnConnectionFailure(true)
            .addInterceptor(commonHeaderInterceptor)
            .addInterceptor(authInterceptor)
            .authenticator(authAuthenticator)
            .addNetworkDebugInterceptors()
            .addChuckerInterceptors(context = context)
            .addLoggingInterceptors()
            .build()
    }

    @Provides
    @Singleton
    @AuthNetwork
    fun provideAuthRetrofit(
        @AuthNetwork authOkHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        return createRetrofit(okHttpClient = authOkHttpClient, json = json)
    }

    @Provides
    @Singleton
    @AuthenticatedNetwork
    fun provideAuthenticatedRetrofit(
        @AuthenticatedNetwork authenticatedOkHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        return createRetrofit(okHttpClient = authenticatedOkHttpClient, json = json)
    }

    private fun createRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .addCallAdapterFactory(ApiResultCallAdapterFactory(json))
            .client(okHttpClient)
            .build()
    }
}
