package com.treestudio.treetask.initializer

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.doannd3.treetask.core.data.worker.SyncWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SyncInitializer @Inject constructor() : AppInitializer {

    // Khởi tạo chạy sau Timber một chút
    override val priority = InitializationPriority.CRITICAL

    override fun init(application: Application) {
        val workManager = WorkManager.getInstance(application)

        // 1. CHUẨN BỊ ĐIỀU KIỆN: Chỉ chạy khi có mạng Internet (Wifi/4G)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // 2. CÁCH 1: SYNC NGAY LẬP TỨC LÚC MỞ APP (Kéo data mới nhất về)
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        // Nếu lúc mở app đang có 1 tiến trình sync chạy dở rồi thì kệ nó chạy tiếp
        workManager.enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            syncRequest,
        )

        // 3. CÁCH 2: SYNC ĐỊNH KỲ (Mỗi 24h chạy ngầm 1 lần để dọn dẹp)
        val periodicRequest = PeriodicWorkRequestBuilder<SyncWorker>(TIME_PERIOD, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            SYNC_PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest,
        )
    }

    companion object {
        private const val TIME_PERIOD = 24L
        private const val SYNC_WORK_NAME = "SyncTaskWork"
        private const val SYNC_PERIODIC_WORK_NAME = "SyncTaskPeriodicWork"
    }
}
