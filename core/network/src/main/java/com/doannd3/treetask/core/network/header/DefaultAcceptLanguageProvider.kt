package com.doannd3.treetask.core.network.header

import javax.inject.Inject

class DefaultAcceptLanguageProvider
@Inject
constructor() : AcceptLanguageProvider {
    override fun getAcceptLanguage(): String = DEFAULT_ACCEPT_LANGUAGE

    private companion object {
        const val DEFAULT_ACCEPT_LANGUAGE = "en"
    }
}
