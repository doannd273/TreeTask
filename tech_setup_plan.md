# Thiết kế Kỹ thuật (Technical Setup Plan)
*Công nghệ bổ trợ, CI/CD, Kiểm soát chất lượng (Lint) và Testing.*

Bản tài liệu này đóng vai trò Architecture Setup Guideline tập trung vào chất lượng code, môi trường build và pipeline phân phối.

---

## 1. Hạ tầng CI/CD (GitHub Actions)

Ứng dụng sẽ có 2 luồng Workflows độc lập trên GitHub Actions bảo vệ nghiêm ngặt source code và tự động hóa khâu phân phối:

### 1.1. Luồng PR Check (`.github/workflows/pr-check.yml`)
- **Trigger**: Khi tạo **Pull Request** hoặc push code lên branch nhắm vào `main` hoặc `develop`.
- **Nhiệm vụ**:
  - Checkout code & Setup JDK 17.
  - Setup Gradle caching (Tối ưu thời gian chạy Action).
  - Chạy `ktlintCheck` (Bảo đảm Clean Format).
  - Chạy `detekt` (Bảo đảm Static Code Quality).
  - Chạy `testDebugUnitTest` (Verify toàn bộ Unit Test).
- **Yêu cầu (Branch Protection):** Bắt buộc Workflow này phải **Passed** thì PR mới được Merge.

### 1.2. Luồng Phân Phối (`.github/workflows/release.yml`)
- **Trigger**: Khi code được **Merge** vào `develop` (Bản Dev) hoặc `main` (Bản Release).
- **Nhiệm vụ**:
  - Lắp ráp `assembleDevRelease` / `assembleProdRelease` hoặc `bundleProdRelease`.
  - Tích hợp **Firebase App Distribution** qua Fastlane hoặc plugin `firebase-appdistribution`. Action tự động push APK lên Firebase và gửi email thông báo cho Tester/QA.

---

## 2. Hệ thống Linting & Formatters

Phân tách trách nhiệm quản lý chất lượng code từ IDE cho đến Pipeline.

* **Ktlint (`org.jlleitschuh.gradle.ktlint`)**:
  - Dùng để chuẩn hóa format code (Spaces, Brackets, Import orders, Naming).
  - Cung cấp tính năng tự động sửa lỗi cơ bản bằng lệnh `./gradlew ktlintFormat`.

* **Detekt (`io.gitlab.arturbosch.detekt`)**:
  - Static Code Analyzer mạnh mẽ cho Kotlin (Bắt lỗi Magic Number, Function/Class quá dài, Complexcity).
  - Config qua file `.detekt.yml`. Rule nghiêm ngặt hơn ktlint, đảm bảo Clean Code tuyệt đối.

* **Git Hooks (Tuỳ chọn)**: 
  - Gắn script shell (`pre-commit`) tự động build `./gradlew detekt ktlintCheck` ngay ở Local mỗi lần Dev gõ lệnh `git commit`. Nếu không pass, không cho phép commit code lên.

---

## 3. Quản lý Môi trường Build Variants & Versioning

Quản lý thông minh bằng **Gradle Version Catalog (`libs.versions.toml`)** để chung một chỗ toàn bộ versions cho cả Multi-module.

Sử dụng Product Flavors và Build Types:

| Flavor | Build Type | Mục đích | API Base Url (Mô phỏng) | Cấu hình thêm |
|---|---|---|---|---|
| `dev` | `debug` | Dev code hằng ngày, Test local | `http://10.0.2.2:5000` | Log level Verbose. Kích hoạt LeakCanary & Chucker.
| `dev` | `release`| QA test nghiệm thu tính năng | `https://api.dev.task.com` | R8/Proguard Bật. Log level Warn/Error. Chucker tắt. LeakCanary tắt.
| `prod`| `debug` | Khắc phục lỗi nóng từ Prod | `https://api.task.com` | Trỏ thẳng server Production thật, có log chucker để soi data thật.
| `prod`| `release`| Bản Final lên **Google Play Store** | `https://api.task.com` | Môi trường sạch, thu nhỏ (Minified), Tắt Log hoàn toàn.

---

## 4. Chiến lược Testing (Test Strategy)

Ứng dụng hướng Clean Architecture (cắt rớp UI vs Logic), sẽ rất tiện để kiểm thử (Testing):

* **Unit Testing (JUnit4 + MockK + Coroutines Test)**:
  - Tầng `core:domain`: Test logic 100% của các UseCase.
  - Tầng `feature:***`: Test các `ViewModels`, kiểm chứng MVI state (`StateFlow` phát đúng chuỗi event Loading -> Success/Error).
* **UI Testing (Compose Test Rule)**:
  - Viết Instrumentation test kiểm tra các Composable phức tạp (VD: Render Form Task, Nút có hoạt động đúng intent không, validate Regex có hiện Error Dialog hay không).

---

## 5. Monitoring & Debugging

Trang bị các công nghệ Monitor "sát thủ" vào App:

1. **Firebase Crashlytics**: Bắt triệt để FATAL Exception (Crash) và tự động vứt vào Firebase Dashboard đính kèm file log của các hàm `Timber.e(...)`.
2. **Firebase Analytics**: Track behavior: Màn hình Login (Bao nhiêu ng log in 1 ngày?), Nút Create Task (Có ai dùng không?).
3. **Firebase Performance**: Track thời gian khởi động app (`cold start`), thời gian API Retrofit chạy quá 2 giây, Screen rendering quá chậm (Jank/Slow frames).
4. **LeakCanary** (chỉ nhúng vào `debug`): Kiểm tra xem có memory leak khi mở qua mở lại Fragment/Activity/Socket connection không (tránh tràn RAM).
5. **Chucker** (chỉ nhúng vào `debug`): Quá trình call API/Socket, tự động push 1 thông báo hiển thị dưới thanh Status Bar, bấm vào mở nguyên bảng Request/Response Header/Body siêu trực quan giống Postman.
6. **Timber**: Unified Logger. Tự bỏ Logcat ở bản Release.

---

## Open Questions

> [!WARNING]
> Bản Setup Plan Kỹ Thuật (CI/CD, Monitoring, Variants) đã được thiết kế sẵn sàng. Trước khi tạo cấu trúc Code, xin làm rõ:
>
> 1. Thiết lập Flavor `dev` debug có nên gán **IP Server nội bộ** (như `10.0.2.2` trỏ vào localhost của Emulator) hay bạn đã có Server Dev public (ví dụ ngrok/vps)?
> 2. Bạn có muốn kích hoạt Git Pre-commit Hook để ép "Máy Dev không được commit mã rác lên Github" hay chỉ cần để CI trên Github check PR là đủ?
