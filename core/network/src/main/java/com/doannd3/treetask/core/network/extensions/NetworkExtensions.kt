package com.doannd3.treetask.core.network.extensions

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.doannd3.treetask.core.network.BuildConfig
import com.doannd3.treetask.core.network.constants.NetworkConstants
import com.doannd3.treetask.core.network.constants.NetworkConstants.READ_TIMEOUT
import com.doannd3.treetask.core.network.constants.NetworkConstants.WRITE_TIMEOUT
import com.doannd3.treetask.core.network.interceptor.NetworkDebugInterceptor
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

fun OkHttpClient.Builder.addChuckerInterceptors(context: Context): OkHttpClient.Builder {
    if (BuildConfig.DEBUG) {
        addInterceptor(
            ChuckerInterceptor.Builder(context)
                .alwaysReadResponseBody(true)
                .build(),
        )
    }
    return this
}

fun OkHttpClient.Builder.addNetworkDebugInterceptors(networkDebugInterceptor: NetworkDebugInterceptor): OkHttpClient.Builder {
    if (BuildConfig.DEBUG) {
        addInterceptor(networkDebugInterceptor)
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

    // tránh lộ token
    loggingInterceptor.redactHeader("Authorization")

    return addInterceptor(loggingInterceptor)
}

fun OkHttpClient.Builder.addTimeout(): OkHttpClient.Builder {
    return connectTimeout(NetworkConstants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
}

fun Headers.redact(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    for (i in 0 until size) {
        val name = name(i)
        val value =
            if (name.equals("Authorization", true)) {
                "██ REDACTED ██"
            } else {
                value(i)
            }
        map[name] = value
    }
    return map
}
