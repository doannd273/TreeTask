# Interview Notes

Tài liệu này giúp giải thích TreeTask khi phỏng vấn hoặc review kiến trúc.

## Elevator pitch

TreeTask là Android task management app xây theo Clean Architecture, MVI và multi-module. App tập trung vào offline-first task list bằng Room/Paging/RemoteMediator, authentication có token refresh/session handling, Compose UI, Hilt DI, và quality gate bằng Detekt/Spotless/unit test.

## Why multi-module?

- Tách trách nhiệm rõ giữa app shell, feature, domain, data, network, database, datastore.
- Feature có thể phát triển độc lập hơn.
- Build logic và dependency dễ kiểm soát hơn bằng convention plugins.
- Test use case/repository không phải kéo toàn bộ app.
- Ranh giới module giúp tránh UI phụ thuộc trực tiếp Retrofit/Room.

## Clean Architecture summary

Dependency đi theo hướng:

```text
feature -> domain -> model
data -> domain/model + network/database/datastore
app -> feature/core composition
```

Ý nghĩa:

- Domain định nghĩa "app làm gì".
- Data định nghĩa "lấy/lưu dữ liệu thế nào".
- Feature định nghĩa "người dùng tương tác thế nào".
- App định nghĩa "các phần được ghép và chạy ra sao".

## MVI summary

Mỗi screen có:

- `State`: dữ liệu render.
- `Event`: hành động từ UI.
- `Effect`: one-shot event như navigate hoặc show error.

Ưu điểm:

- UI dễ preview/test vì chỉ cần state.
- Logic tập trung trong ViewModel.
- Side effect không bị trộn vào state render.
- Flow state/effect rõ ràng và lifecycle-aware.

## Offline-first explanation

Task list dùng Paging 3:

- UI observe `PagingData<Task>`.
- `TaskRepositoryImpl` tạo `Pager`.
- `TaskRemoteMediator` sync API vào Room.
- Room là local cache/source cho list.
- Network fail vẫn có thể hiển thị dữ liệu cache nếu đã có.

Điểm cần nói thẳng:

- Query/filter cache hiện là debt cần cải thiện để scope theo `userId`, `status`, `keyword`.
- Debt này đã được ghi trong `docs/ARCHITECTURE_DEBT.md`.

## Authentication/session

- App kiểm tra token/profile ở `MainViewModel` để chọn start destination.
- Auth repository quản lý login/register/forgot password.
- Token storage nằm ở `core:datastore`.
- OkHttp interceptor/authenticator xử lý auth header/token refresh.
- Khi session expired, app/root layer đưa người dùng về auth graph.

## Build logic

Repo có `build-logic` để gom cấu hình lặp:

- Android application/library
- Compose
- Hilt/KSP
- Desugar
- Detekt
- Spotless

Lợi ích:

- Module build file ngắn hơn.
- SDK/JVM target thống nhất.
- Dễ rollout convention mới.

## Quality gates

Local CI:

```bash
./scripts/run-ci.sh
```

Chạy:

- `spotlessCheck`
- `detekt`
- `testDebugUnitTest`
- `assembleDebug`

Testing stack:

- JUnit4
- MockK
- Truth
- Turbine
- kotlinx-coroutines-test
- `MainDispatcherRule` trong `core:testing`

## Trade-offs and known debt

Một số điểm cố ý giữ lại/đã biết:

- `core:common` đang chứa cả common thuần và presentation helper (`UiText`, `BaseViewModel`).
- `core:designsystem` đang chứa app-level global state cho dialog/loading.
- `core:network` đang phụ thuộc datastore cho token/device storage.
- Một số feature còn có dependency có thể cleanup.
- Một số fallback error text còn hardcode tiếng Việt.

Cách trả lời tốt:

"Project ưu tiên deliver kiến trúc rõ và chạy được trước. Các debt không bị bỏ quên mà được ghi lại trong `docs/ARCHITECTURE_DEBT.md`, sau đó cleanup theo phạm vi nhỏ để tránh refactor lớn làm tăng rủi ro."

## Good talking points

- "Feature chỉ nên biết use case, không biết service/DAO."
- "Repository implementation là nơi phối hợp network, database, datastore."
- "MVI giúp Compose render từ state và xử lý one-shot effect rõ ràng."
- "Convention plugin giúp giảm copy-paste Gradle."
- "Offline-first dùng Room làm cache và RemoteMediator làm cầu nối network."
- "Global navigation và app state ở app module, feature expose graph thôi."

## Possible interview questions

**Vì sao không để ViewModel gọi Retrofit trực tiếp?**

Vì ViewModel thuộc presentation layer. Gọi Retrofit trực tiếp sẽ làm UI phụ thuộc network implementation, khó test và khó đổi data source. Use case/repository tạo boundary sạch hơn.

**Vì sao dùng Effect thay vì đặt navigation flag trong State?**

Navigation là one-shot side effect. Nếu đặt trong State, recomposition hoặc collect lại có thể trigger lại navigation. `SharedFlow` effect phù hợp hơn.

**Vì sao cần `core:model` riêng?**

Model được chia sẻ giữa domain/data/feature mà không kéo dependency implementation. Nó giữ type ổn định cho business data.

**Khi thêm API mới sẽ làm thế nào?**

Thêm DTO/service ở `core:network`, contract/use case ở `core:domain`, implementation/mapper ở `core:data`, bind Hilt nếu cần, rồi feature inject use case.

**Khi app offline thì task list hoạt động thế nào?**

UI đọc dữ liệu qua Paging từ Room. RemoteMediator cố sync network; nếu network fail, cache local vẫn có thể hiển thị dữ liệu đã lưu.
