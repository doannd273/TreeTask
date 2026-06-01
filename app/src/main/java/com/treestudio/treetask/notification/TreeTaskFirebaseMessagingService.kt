package com.treestudio.treetask.notification

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.dispatcher.Dispatcher
import com.doannd3.treetask.core.common.dispatcher.TreeTaskDispatchers
import com.doannd3.treetask.core.common.log.AppTag
import com.doannd3.treetask.core.domain.usecase.device.RegisterDeviceTokenIfAuthenticatedUseCase
import com.doannd3.treetask.core.notification.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TreeTaskFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var registerDeviceTokenIfAuthenticatedUseCase: RegisterDeviceTokenIfAuthenticatedUseCase

    @Inject
    @Dispatcher(TreeTaskDispatchers.IO)
    lateinit var ioDispatcher: CoroutineDispatcher

    private val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Timber.tag(AppTag.NETWORK).e(throwable, "Register FCM token failed unexpectedly")
        }

    private val serviceScope by lazy {
        CoroutineScope(SupervisorJob() + ioDispatcher)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: return
        val body = remoteMessage.notification?.body ?: return
        val taskId = remoteMessage.data["taskId"] ?: ""
        val notificationId = taskId.hashCode()
        notificationHelper.showTaskReminderNotification(
            notificationId = notificationId,
            title = title,
            body = body,
            taskId = taskId,
        )
    }

    override fun onNewToken(token: String) {
        serviceScope.launch(exceptionHandler) {
            val result = registerDeviceTokenIfAuthenticatedUseCase(token)
            when (result) {
                null -> {
                    Timber.tag(AppTag.NETWORK).d("Skip FCM token registration: user not logged in")
                }

                is ApiResult.Success -> {
                    Timber.tag(AppTag.NETWORK).d("FCM token registered")
                }

                is ApiResult.Error -> {
                    Timber.tag(AppTag.NETWORK).w(
                        result.exception,
                        "Register FCM token failed: ${result.backendErrorCode}",
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
