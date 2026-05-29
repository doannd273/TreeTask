package com.doannd3.treetask.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gom toàn bộ logic setup và hiển thị notification của app vào một chỗ.
 *
 * Mục tiêu là không rải code NotificationManager/Intent/PendingIntent ở nhiều feature.
 * App gọi [createChannel] một lần khi khởi động, còn nơi nào quyết định cần nhắc task
 * thì gọi [showTaskReminderNotification].
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    /**
     * Đăng ký notification channel cho nhóm nhắc task.
     *
     * Android 8.0+ bắt buộc notification phải thuộc một channel. Android 7.1 trở xuống
     * chưa có khái niệm channel, nên thoát sớm để tránh gọi API 26 trên device minSdk 24.
     * Gọi lại hàm này nhiều lần vẫn an toàn, vì Android giữ các thiết lập channel mà user
     * đã chỉnh sau lần tạo đầu.
     */
    fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            NotificationChannels.TASK_REMINDER,
            context.getString(R.string.notification_channel_task_reminder_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.notification_channel_task_reminder_description)
        }

        // getSystemService có thể null trên một số device lạ, nên tạo channel theo hướng null-safe.
        context.getSystemService<NotificationManager>()?.createNotificationChannel(channel)
    }

    /**
     * Tạo và hiển thị local notification cho một task reminder.
     *
     * [notificationId] quyết định hành vi replace: dùng cùng id thì update notification cũ,
     * dùng id mới thì Android hiển thị notification mới.
     *
     * [taskId] được gắn vào launch intent để MainActivity/navigation có thể mở đúng task sau này.
     * Helper này chỉ đóng gói dữ liệu; app layer sẽ quyết định consume extra đó như thế nào.
     */
    fun showTaskReminderNotification(
        notificationId: Int,
        title: String,
        body: String,
        taskId: String,
    ) {
        // Dùng lại launcher intent của app để khi user bấm notification thì mở TreeTask.
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            // SINGLE_TOP tránh tạo Activity mới nếu Activity hiện tại có thể nhận intent này.
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_TASK_ID, taskId)
        }

        // getLaunchIntentForPackage có thể null nếu app không có launchable Activity.
        // Khi đó vẫn show notification, chỉ bỏ action mở app khi user bấm notification.
        val pendingIntent = launchIntent?.let { intent ->
            // PendingIntent là "vé ủy quyền" Android giữ lại và chạy sau khi user bấm notification.
            PendingIntent.getActivity(
                context,
                // requestCode gắn với notificationId để mỗi reminder có thể mang extra riêng.
                notificationId,
                intent,
                // UPDATE_CURRENT thay extra cũ; IMMUTABLE là flag nên dùng trên Android hiện đại.
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        // NotificationCompat giúp code notification ổn định hơn giữa nhiều version Android.
        val notificationBuilder = NotificationCompat.Builder(context, NotificationChannels.TASK_REMINDER)
            .setSmallIcon(R.drawable.notification_ic_task_reminder)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)

        pendingIntent?.let(notificationBuilder::setContentIntent)

        val notification = notificationBuilder.build()

        // Android 13+ chỉ hiển thị nếu app đã được cấp quyền POST_NOTIFICATIONS.
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    companion object {
        // Intent extra để app navigation đọc sau khi notification mở app.
        const val EXTRA_TASK_ID = "EXTRA_TASK_ID"
    }
}
