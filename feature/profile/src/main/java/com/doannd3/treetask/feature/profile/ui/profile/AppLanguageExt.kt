package com.doannd3.treetask.feature.profile.ui.profile

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.doannd3.treetask.core.model.profile.AppLanguage
import com.doannd3.treetask.feature.profile.R

@StringRes
fun AppLanguage.displayNameResId(): Int =
    when (this) {
        AppLanguage.ENGLISH -> R.string.profile_language_en
        AppLanguage.VIETNAMESE -> R.string.profile_language_vi
    }

@DrawableRes
fun AppLanguage.flagIconRes(): Int =
    when (this) {
        AppLanguage.ENGLISH -> R.drawable.profile_ic_flag_en
        AppLanguage.VIETNAMESE -> R.drawable.profile_ic_flag_vi
    }

@StringRes
fun AppLanguage.nativeNameResId(): Int =
    when (this) {
        AppLanguage.ENGLISH -> R.string.profile_language_en_native
        AppLanguage.VIETNAMESE -> R.string.profile_language_vi_native
    }
