package com.treestudio.treetask.locale

import androidx.appcompat.app.AppCompatDelegate
import com.doannd3.treetask.core.model.profile.AppLanguage
import com.doannd3.treetask.core.network.header.AcceptLanguageProvider
import java.util.Locale
import javax.inject.Inject

class AppLocaleAcceptLanguageProvider
@Inject
constructor() : AcceptLanguageProvider {
    override fun getAcceptLanguage(): String {
        val appLanguage = AppCompatDelegate.getApplicationLocales().get(0)?.language
        return AppLanguage.normalizeLocaleTag(localeTag = (appLanguage ?: Locale.getDefault().language))
    }
}
