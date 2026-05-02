package com.doannd3.treetask.core.network.extensions

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.doannd3.treetask.core.network.BuildConfig
import com.doannd3.treetask.core.network.constants.NetworkConstants
import com.doannd3.treetask.core.network.constants.NetworkConstants.READ_TIMEOUT
import com.doannd3.treetask.core.network.constants.NetworkConstants.WRITE_TIMEOUT
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

fun OkHttpClient.Builder.addDebugInterceptors(context: Context): OkHttpClient.Builder {
    if (BuildConfig.DEBUG) {
        addInterceptor(ChuckerInterceptor(context))
    }
    return this
}

fun OkHttpClient.Builder.addLoggingInterceptors(): OkHttpClient.Builder {
    val loggingInterceptor =
        HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
        }
    return addInterceptor(loggingInterceptor)
}

fun OkHttpClient.Builder.addTimeout(): OkHttpClient.Builder {
    return connectTimeout(NetworkConstants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
}
