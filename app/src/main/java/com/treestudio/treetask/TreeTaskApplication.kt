package com.treestudio.treetask

import android.app.Application
import com.treestudio.treetask.initializer.AppInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TreeTaskApplication : Application() {

    @Inject
    lateinit var initializers: Set<@JvmSuppressWildcards AppInitializer>

    override fun onCreate() {
        super.onCreate()

        initializers.sortedByDescending { it.priority.value }.forEach {
            it.init(this)
        }
    }
}
