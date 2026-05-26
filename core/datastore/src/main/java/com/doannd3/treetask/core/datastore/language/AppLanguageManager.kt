package com.doannd3.treetask.core.datastore.language

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.doannd3.treetask.core.datastore.extensions.appLanguageDataStore
import com.doannd3.treetask.core.model.profile.AppLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLanguageManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : AppLanguageStorage {

    override fun getAppLanguage(): Flow<AppLanguage> {
        return context.appLanguageDataStore.data.map { prefs ->
            val appLanguageTag = prefs[APP_LANGUAGE_KEY]
            if (appLanguageTag != null) {
                AppLanguage.fromLocaleTag(localeTag = appLanguageTag)
            } else {
                getDefaultAppLanguage()
            }
        }
    }

    private fun getDefaultAppLanguage(): AppLanguage {
        val systemLanguage = Locale.getDefault().language
        return AppLanguage.fromLocaleTag(systemLanguage)
    }

    override suspend fun saveAppLanguage(appLanguage: AppLanguage) {
        context.appLanguageDataStore.edit { prefs ->
            prefs[APP_LANGUAGE_KEY] = appLanguage.localeTag
        }
    }

    override suspend fun clearAppLanguage() {
        context.appLanguageDataStore.edit { it.clear() }
    }

    companion object {
        private val APP_LANGUAGE_KEY = stringPreferencesKey("APP_LANGUAGE_KEY")
    }
}
