package com.treestudio.treetask.initializer

enum class InitializationPriority(val value: Int) {
    // 1. Phải chạy đầu tiên. Thường là công cụ bắt lỗi, log, bảo mật.
    // Vì nếu app lỗi ngay lúc khởi động, bọn này phải đứng đó để ghi nhận.
    URGENT(100),

    // 2. Chạy thứ hai. Thường là thiết lập Database cục bộ (Room), Network Config.
    CRITICAL(75),

    // 3. Chạy bình thường (Mặc định). Các logic nghiệp vụ chung.
    NORMAL(50),

    // 4. Chạy cuối cùng (Hoặc thậm chí đẩy ra chạy ngầm).
    // VD: Các công cụ Tracking hành vi người dùng (Analytics, Facebook Pixel)
    // không được phép làm nghẽn quá trình bật app.
    LAZY(10),
}
