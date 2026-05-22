package com.doannd3.treetask.core.model.profile

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val localeTag: String,
) {
    VIETNAMESE("vn", "Tiếng việt", "vi"),
    ENGLISH("en", "English", "en"),
    ;

    companion object {
        fun fromCode(code: String): AppLanguage {
            return AppLanguage.entries.firstOrNull { it.code == code } ?: VIETNAMESE
        }
    }
}
