package com.treestudio.treetask.initializer

import android.app.Application
import com.treestudio.treetask.BuildConfig
import timber.log.Timber
import javax.inject.Inject

class TimberInitializer @Inject constructor() : AppInitializer {

    // Timber phải chạy cực sớm!
    override val priority = InitializationPriority.URGENT

    override fun init(application: Application) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}