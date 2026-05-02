package com.treestudio.treetask.initializer

import android.app.Application

interface AppInitializer {
    // Giá trị mặc định là NORMAL, dev có thể đè (override) lại nếu cần thiết
    val priority: InitializationPriority get() = InitializationPriority.NORMAL

    fun init(application: Application)
}
