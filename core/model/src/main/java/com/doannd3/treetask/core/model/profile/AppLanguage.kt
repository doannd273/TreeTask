package com.doannd3.treetask.core.model.profile

enum class AppLanguage(
    val displayName: String,
    val localeTag: String,
) {
    VIETNAMESE("Tiếng việt", "vi"),
    ENGLISH("English", "en"),
    ;

    companion object {
        fun fromLocaleTag(localeTag: String): AppLanguage {
            return AppLanguage.entries.firstOrNull { it.localeTag == localeTag } ?: ENGLISH
        }

        fun normalizeLocaleTag(localeTag: String?): String {
            return localeTag?.takeIf { tag -> entries.any { it.localeTag == tag } }
                ?: ENGLISH.localeTag
        }
    }
}
