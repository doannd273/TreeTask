# Kế hoạch Triển khai Ứng dụng Android Task Manager (Chi tiết Solution Architecture)

Bản kế hoạch này được cập nhật sau khi phân tích chi tiết mã nguồn Backend API (`task-backend`). Dưới đây là kiến trúc hệ thống chuẩn xác, bám sát các endpoint thực tế và mô tả chi tiết setup từng tính năng/màn hình.

## 1. Cấu trúc Kiến trúc Hệ thống (Now In Android Pattern)

Ứng dụng sẽ áp dụng mô hình **Clean Architecture + Multi-module + MVI Pattern**, quản lý state bằng `StateFlow`/`SharedFlow`.

### Sơ đồ Module Core & Tính năng:
- **`app`**: File khởi chạy, setup Hilt DI, Navigation Graph tổng.
- **`core:model`**: Định nghĩa Model gốc dựa trên BE Response (`Task`, `User`, `Conversation`, `Message`).
- **`core:network`**:
    - Tích hợp Retrofit, `Kotlinx.serialization`.
    - Cấu hình Wrapper Response chung: `ApiResponse<T>` map chính xác JSON:
      `{ "success": Boolean, "message": String?, "data": T? }`.
    - Thiết lập **Authenticator Interceptor** bắt lỗi HTTP 401 tự động gọi `/api/auth/refresh-token`.
    - Thiết lập **Socket.IO Client v4.8+** (tương thích backend), quản lý connection stream qua `Flow`.
- **`core:database`**: Thiết lập RoomDB với các Entity Local để offline caching.
- **`core:datastore`**: Tích hợp Preferences DataStore (như bạn yêu cầu) để lưu `accessToken` (giới hạn 15m) và `refreshToken` (7d).
- **`core:data`**: Tầng Interface & Implementation của các Repositories, kết hợp `RemoteMediator` (Paging 3) và Room fallback kết nối mạng (NetworkBoundResource).
- **`core:designsystem`**: Theme mặc định, Typography, Component dùng chung.

---

## 2. Kế hoạch Triển khai Từng Màn Hình (Screen-by-Screen)

### 2.1. Feature: Auth (`feature:auth`)
Dùng cho luồng đăng nhập, đăng ký và điều hướng ban đầu.

* **Splash Screen (`SplashScreen`)**:
    - **Setup**: Màn hình hiển thị logo app tức thời. ViewModel gọi Local DataStore để kiểm tra `accessToken`.
    - **Logic**: Nếu có token, gọi API `GET /api/user/profile`.
        - Thành công → Navigate tới `MainActivity` (Nhánh Home).
        - Lỗi 401 (hoặc null) → Chạy cơ chế refresh token. Nếu refresh fail → Navigate tới `LoginScreen`.

* **Login Screen (`LoginScreen`)**:
    - **Setup**: 2 TextFields (Email bắt Format trùng DB lowercase, Mật khẩu), Nút Login, Chữ "Đăng ký".
    - **Logic**: Khi click Login, ViewModel `POST /api/auth/login`. Lấy `data.accessToken` và `data.refreshToken` lưu vào DataStore. Connect Socket ngay sau khi có token.

* **Register Screen (`RegisterScreen`)**:
    - **Setup**: Tương tự login.
    - **Logic**: `POST /api/auth/register`. Đăng ký thành công Backend sẽ trả về Token (giống hệt Login). Ứng dụng tự động lưu DataStore và Navigate vào `MainActivity` không bắt đăng nhập lại.

### 2.2. Feature: Task Management (`feature:tasks`)
Dựa vào các API Task CRUD ở Backend.

* **Danh sách Task (`TasksListScreen`)**:
    - **Setup**: `LazyColumn` bọc trong `PullRefresh`. Một thanh Search Bar (áp dụng debounce 500ms API calls) và Filter Dropdown (All, `todo`, `in_progress`, `pending`, `done`).
    - **Logic**: Dùng **Paging 3** + Room Remote Mediator liên kết với `GET /api/tasks?page=1&limit=20&status=&keyword=`.
    - **Offline support**: Khi đọc API lỗi vì rớt mạng, Room `TaskEntity` sẽ trả ra kết quả truy vấn offline.

* **Add / Edit Task (`TaskBottomSheet`)**:
    - **Setup**: `ModalBottomSheet` hiển thị Title, Description, Status Select (Tương đương db backend).
    - **Logic**: Dùng `POST /api/tasks` hoặc `PUT /api/tasks/:id`.
    - **Offline fallback**: Nếu không có mạng lúc post, tạo/update đối tượng ở Room + flag `sync=PENDING_CREATE/UPDATE` và enqueue Job vào **WorkManager**. Khi có wifi, enqueue trigger gọi API.

* **Xoá / Phục hồi Task**:
    - **Logic**: Backend dùng delete hard (xoá vật lý `DELETE /api/tasks/:id`). Mobile gọi hàm xoá và show `Snackbar` "Đã xoá". Chú ý Backend không có cơ chế khôi phục (soft-delete), nên tính năng "Khôi phục task" trên Mobile sẽ là **cache lại ở DB Mobile** 2-3s trước khi call Delete API thực.

### 2.3. Feature: Stats (`feature:stats`)
Thống kê tổng quan trạng thái công việc.

* **Stats Dashboard Screen (`StatsScreen`)**:
    - **Setup**: Layout bao gồm Component PieChart/BarChart (Ví dụ dùng `Vico` hoặc `MPAndroidChart`) và Danh sách hiển thị `Recent Tasks`.
    - **Logic**: Fetch API `GET /api/tasks/stats`. Backend sẽ trả về count: (`total`, `todo`, `in_progress`, `pending`, `done`) và danh sách `recentTasks`.

### 2.4. Feature: Chat & Realtime (`feature:chat`)
Tích hợp trực tiếp với Backend `conversationController`, `messageController` và cấu trúc Socket.

* **Danh sách Hội Thoại (`ConversationsScreen`)**:
    - **Setup**: Paged `LazyColumn` hiển thị lịch sử Group/Private Chat kèm Avatar, Badge New Message.
    - **Logic**: Fetch API `GET /api/conversations`. Trả về Array conversations có chứa block `lastMessage`.
    - **Action tạo nhóm**: FAB mở Dialog -> Gọi `GET /api/users/search` (tìm user) -> `POST /api/conversations` với `type` (private/group) và `participantIds`.

* **Chi tiết Hội thoại (`ChatDetailScreen`)**:
    - **Setup**: Toolbar hiển thị Avatar, Title (Chat 1-1 hiển thị tên user, Group hiển thị tên nhóm). Danh sách tin nhắn `LazyColumn` đảo ngược hướng (`reverseLayout = true`).
    - **Logic REST**: Load Paging lịch sử qua `GET /api/conversations/:id/messages`.
    - **Logic Socket**:
        - Khi vào màn này: Emit `join_conversation` ID.
        - Nhắn tin: `POST /api/messages` hoặc Emit message tùy thiết kế backend. Đồng thời nghe ngóng event `new_message` bằng StateFlow trong ViewModel.
        - Typing status: Emit `typing_start` / `typing_stop`.

* **Quản trị Nhóm (`GroupInfoBottomSheet`)**:
    - Dành cho `type == group`. Creator có quyền:
        - Đổi tên: `PUT /api/conversations/:id`.
        - Thêm member: Tìm user từ API search -> gọi API thêm vào Room.
        - Xóa member / Xóa phòng: `DELETE /api/conversations/:id/participants/:userId`.

### 2.5. Feature: Profile (`feature:profile`)
Quản lý trang cá nhân người dùng.

* **Profile Screen (`ProfileScreen`)**:
    - **Setup**: Hiển thị Avatar tròn (load bằng Coil), hiển thị Email, Số điện thoại.
    - **Logic**: Fetch API `GET /api/user/profile` (lấy User info).

* **Đổi Avatar & Số điện thoại (`EditProfileDialog`)**:
    - **Logic Avatar**: Dùng image picker bọc ảnh, tạo `MultipartBody.Part` rồi gửi `POST /api/user/avatar`. Backend trả về dạng `/uploads/avatars/...`. Nối với baseUrl để coil load.
    - **Logic Phone**: Cập nhật bằng API `PUT /api/user/profile`.

* **Đổi mật khẩu / Đăng xuất**:
    - Form dialog dùng API `PUT /api/user/password`.
    - Logic Logout: gọi backend `logout` API (clear refresh token db), clear Preferences DataStore Mobile, clear Paging Cache Room, Ngắt Socket -> Navigate về lại `LoginScreen`.

---

## User Review Required

> [!IMPORTANT]
> Bản Architect và Detail Roadmap đã được chuẩn hoá bám sát sườn Code API thực tế của bạn. Dưới đây là 2 yếu tố quyết định cần bạn duyệt trước khi tôi tiến hành tạo cấu trúc thư mục code:
>
> 1. **Kiến trúc DB Local: "Backend Delete Hard - App Soft Delete"**
     > Backend API hiện tại đang `Task.findOneAndDelete()` (Xóa cứng), nên phần bạn yêu cầu "có thể khôi phục qua nút Restore" ở bước xoá tôi sẽ phải làm giả lập (delay xóa DB Server vài giây, hoặc đánh cờ deleted trên RommDB, User bấm Undo sẽ huỷ enqueue request gọi API Delete). Bạn đồng ý cơ chế này chứ?
>
> 2. **Flow bắt đầu khởi tạo System:**
     > Tôi sẽ bắt đầu tạo `libs.versions.toml`, cấu trúc base project `app`, và `core` modules (network + Retrofit setup) trước. Rất mong bạn **Phê Duyệt** (Approve) plan này để tôi tiến hành bước đầu tiên.
