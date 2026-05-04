package com.treestudio.treetask.initializer

import android.app.Application
import com.doannd3.treetask.core.analytics.AnalyticsHelper
import com.doannd3.treetask.core.common.dispatcher.Dispatcher
import com.doannd3.treetask.core.common.dispatcher.TreeTaskDispatchers
import com.doannd3.treetask.core.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class AnalyticsSessionManager @Inject constructor(
    private val userRepository: UserRepository,
    private val analyticsHelper: AnalyticsHelper,
    @Dispatcher(TreeTaskDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : AppInitializer {
    private val applicationScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    override fun init(application: Application) {
        applicationScope.launch {
            userRepository.getCachedProfile().collect { user ->
                if (user != null) {
                    analyticsHelper.setUserId(user.id)
                } else {
                    analyticsHelper.clearUserId()
                }
            }
        }
    }
}
