package com.treestudio.treetask

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.treestudio.treetask.initializer.AppInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TreeTaskApplication :
    Application(),
    Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var initializers: Set<@JvmSuppressWildcards AppInitializer>

    override fun onCreate() {
        super.onCreate()

        initializers.sortedByDescending { it.priority.value }.forEach {
            it.init(this)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
