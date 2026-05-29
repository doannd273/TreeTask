package com.treestudio.treetask.notification

import com.doannd3.treetask.core.notification.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TreeTaskFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

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
        // TODO
    }
}
