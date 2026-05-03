package com.doannd3.treetask.core.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.datastore.UserPrefsManager
import com.doannd3.treetask.core.domain.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

@HiltWorker
class SyncWorker
@AssistedInject
constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskRepository: TaskRepository,
    private val userPrefsManager: UserPrefsManager,
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        Timber.i("SyncWorker: Bắt đầu quá trình đồng bộ hóa dữ liệu ngầm...")

        // 1. Lấy thông tin User hiện tại (hứng phần tử đầu tiên của Flow)
        val user = userPrefsManager.getUserProfile().firstOrNull()

        // Nếu user đã đăng xuất, hủy luôn nhiệm vụ này (failure), không cần làm gì cả
        if (user == null || user.id.isBlank()) {
            Timber.w("SyncWorker: Không có User đăng nhập. Hủy đồng bộ.")
            return Result.failure()
        }

        // 2. Kêu gọi TaskRepository đồng bộ dữ liệu của ông user này
        return when (val result = taskRepository.syncTasks(user.id)) {
            is ApiResult.Success -> {
                Timber.i("SyncWorker: Đồng bộ THÀNH CÔNG!")
                Result.success() // Báo cáo sếp: Đã xong việc!
            }

            is ApiResult.Error -> {
                Timber.e(result.exception, "SyncWorker: Đồng bộ THẤT BẠI.")
                // Báo cáo sếp: Xin hãy gọi em dậy chạy lại nhiệm vụ này khi mạng ổn định hơn!
                Result.retry()
            }
        }
    }
}
