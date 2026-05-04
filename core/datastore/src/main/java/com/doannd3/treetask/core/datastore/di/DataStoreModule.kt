package com.doannd3.treetask.core.datastore.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.doannd3.treetask.core.datastore.device.DevicePrefsManager
import com.doannd3.treetask.core.datastore.device.DeviceStorage
import com.doannd3.treetask.core.datastore.token.TokenManager
import com.doannd3.treetask.core.datastore.token.TokenStorage
import com.doannd3.treetask.core.datastore.user.UserPrefsManager
import com.doannd3.treetask.core.datastore.user.UserStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

// ─── Qualifier ────────────────────────────────────────────────────────────────

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EncryptedPrefs

// ─── DI Module ────────────────────────────────────────────────────────────────

@Module
@InstallIn(SingletonComponent::class)
interface DataStoreModule {
    @Binds
    @Singleton
    fun bindTokenStorage(impl: TokenManager): TokenStorage

    @Binds
    @Singleton
    fun bindUserStorage(impl: UserPrefsManager): UserStorage

    @Binds
    @Singleton
    fun bindDeviceStorage(impl: DevicePrefsManager): DeviceStorage

    companion object {
        @Provides
        @Singleton
        @EncryptedPrefs
        fun provideEncryptedSharedPreferences(
            @ApplicationContext context: Context,
        ): SharedPreferences {
            val masterKey =
                MasterKey
                    .Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

            return EncryptedSharedPreferences.create(
                context,
                "secure_token_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        }
    }
}
