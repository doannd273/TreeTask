package com.doannd3.treetask.feature.profile.ui.profile

import com.doannd3.treetask.core.model.profile.AppLanguage
import com.doannd3.treetask.feature.profile.R

fun AppLanguage.displayNameResId(): Int =
    when (this) {
        AppLanguage.ENGLISH -> R.string.profile_language_en
        AppLanguage.VIETNAMESE -> R.string.profile_language_vi
    }

fun AppLanguage.flagIconRes(): Int =
    when (this) {
        AppLanguage.ENGLISH -> R.drawable.profile_ic_flag_en
        AppLanguage.VIETNAMESE -> R.drawable.profile_ic_flag_vi
    }

fun AppLanguage.nativeName(): String =
    when (this) {
        AppLanguage.ENGLISH -> "English"
        AppLanguage.VIETNAMESE -> "Tiếng Việt"
    }
