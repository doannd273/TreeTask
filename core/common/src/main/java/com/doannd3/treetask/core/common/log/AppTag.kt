package com.doannd3.treetask.core.common.log

/**
 * Tập trung toàn bộ Timber TAG của project tại đây.
 *
 * CÁCH DÙNG:
 *   Timber.tag(AppTag.NETWORK).d("Gọi API login...")
 *   Timber.tag(AppTag.AUTH).i("User đăng nhập thành công")
 *
 * CÁCH FILTER LOGCAT:
 *   tag:TT          → Toàn bộ log của app TreeTask
 *   tag:TT:AppInfo  → Log lúc khởi động app
 *   tag:TT:Auth     → Log luồng đăng nhập / đăng xuất
 *   tag:TT:Network  → Log API request / response
 *   tag:TT:Tasks    → Log luồng CRUD Task
 *   tag:TT:Sync     → Log WorkManager background sync
 *   tag:TT:DB       → Log Room database operations
 *   tag:TT:Chat     → Log Socket.IO realtime chat
 *   tag:TT:Nav      → Log Navigation events
 */
object AppTag {
    // Prefix chung cho toàn app → filter Logcat bằng: tag:TT
    private const val PREFIX = "TreeTaskTag"

    // App lifecycle
    const val APP_INFO = "$PREFIX:AppInfo"

    // Features
    const val AUTH = "$PREFIX:Auth"
    const val TASKS = "$PREFIX:Tasks"
    const val CHAT = "$PREFIX:Chat"
    const val PROFILE = "$PREFIX:Profile"
    const val STATS = "$PREFIX:Stats"
    const val SYNC = "$PREFIX:Sync"
    const val NAV = "$PREFIX:Nav"
    const val VIEW_MODEL = "$PREFIX:ViewModel"

    // Core infrastructure
    const val NETWORK = "$PREFIX:Network"
    const val DATABASE = "$PREFIX:Database"
    const val DATA = "$PREFIX:Data"
    const val COMMON = "$PREFIX:Common"
}
