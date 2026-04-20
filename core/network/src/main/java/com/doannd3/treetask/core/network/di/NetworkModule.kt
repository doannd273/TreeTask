package com.doannd3.treetask.core.network.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.doannd3.treetask.core.network.BuildConfig
import com.doannd3.treetask.core.network.interceptor.AuthAuthenticator
import com.doannd3.treetask.core.network.interceptor.AuthInterceptor
import com.doannd3.treetask.core.network.interceptor.HeaderInterceptor
import com.doannd3.treetask.core.network.service.AuthService
import com.doannd3.treetask.core.network.service.ChatService
import com.doannd3.treetask.core.network.service.StatsService
import com.doannd3.treetask.core.network.service.TaskService
import com.doannd3.treetask.core.network.service.UserService
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
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

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
            // coerceInputValues = true
            // Format date, naming strategy (nếu cần)
            // encodeDefaults = true
        }
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(
        @ApplicationContext context: Context,
        authInterceptor: AuthInterceptor,
        authAuthenticator: AuthAuthenticator,
        headerInterceptor: HeaderInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(headerInterceptor) // 1. thêm header
            .addInterceptor(loggingInterceptor) // 2. log request
            .addInterceptor(ChuckerInterceptor(context)) // 3. Log vào Chucker UI
            .addInterceptor(authInterceptor) // 4. Thêm Token Auth
            .authenticator(authAuthenticator).build()
        return okHttpClient
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .addCallAdapterFactory(ApiResultCallAdapterFactory(json))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideTaskService(retrofit: Retrofit): TaskService {
        return retrofit.create(TaskService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideStatsService(retrofit: Retrofit): StatsService {
        return retrofit.create(StatsService::class.java)
    }

    @Provides
    @Singleton
    fun provideChatService(retrofit: Retrofit): ChatService {
        return retrofit.create(ChatService::class.java)
    }

    companion object {
        private const val NETWORK_TIMEOUT = 30L
    }
}