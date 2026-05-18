# Human Guide VI

File này là bản hướng dẫn tiếng Việt cho Đoàn đọc nhanh. Nó không thay thế các docs tiếng Anh.

Quy ước của project:

- Các file technical docs tiếng Anh là nguồn chuẩn cho Codex/code agent.
- File này là bản đồ đọc docs, giải thích ngắn gọn bằng tiếng Việt.
- Nếu nội dung file này lệch với docs tiếng Anh hoặc code hiện tại, ưu tiên code hiện tại trước, sau đó cập nhật docs tiếng Anh.

## Nên đọc file nào khi làm việc?

Khi muốn hiểu kiến trúc tổng thể:

- Đọc `docs/ARCHITECTURE.md`.
- File này giải thích module `app`, `core:*`, `feature:*`, Clean Architecture, MVI, data flow, navigation, offline-first.

Khi muốn biết convention code:

- Đọc `docs/CODING_CONVENTIONS.md`.
- File này nói về Kotlin style, Gradle, Compose, MVI, Data/Domain, error handling, testing.

Khi muốn thêm feature hoặc screen mới:

- Đọc `docs/FEATURE_TEMPLATE.md`.
- File này là checklist scaffold feature module, MVI files, navigation, domain/data, tests.

Khi muốn sửa UI:

- Đọc `docs/UI_GUIDELINES.md`.
- File này nói về Compose structure, design system, loading/error/empty state, text/localization, accessibility.

Khi muốn biết còn nợ kiến trúc gì:

- Đọc `docs/ARCHITECTURE_DEBT.md`.
- File này liệt kê tech debt đang biết, mức độ ưu tiên, impact và hướng xử lý.

Khi chuẩn bị phỏng vấn hoặc giải thích project:

- Đọc `docs/INTERVIEW_NOTES.md`.
- File này giữ tiếng Việt vì phục vụ Đoàn nói chuyện/phỏng vấn.

Khi muốn agent hiểu cách làm việc trong repo:

- Đọc `AGENTS.md`.
- File này là instruction tổng cho Codex/AI agent: build/test, module boundary, Clean Architecture, MVI, UI conventions.

## Luồng code feature chuẩn

Khi thêm một feature hoặc màn hình mới, đi theo thứ tự này:

1. Xác định feature thuộc module nào: `feature:tasks`, `feature:auth`, `feature:profile`, v.v.
2. Tạo hoặc sửa contract: `State`, `Event`, `Effect`.
3. ViewModel chỉ inject use case từ `core:domain`.
4. Nếu cần data mới, thêm contract/use case ở `core:domain`.
5. Implementation repository đặt ở `core:data`.
6. API DTO/service đặt ở `core:network`.
7. Room entity/DAO đặt ở `core:database`.
8. UI Compose chỉ gọi `onEvent(...)`, không gọi repository/use case trực tiếp.
9. Navigation helper đặt trong `feature/<name>/navigation`.
10. Test use case/repository/ViewModel nếu logic có branch quan trọng.

## Module hiểu nhanh

`app`:

- App shell.
- Chứa `MainActivity`, `TreeTaskApp`, global navigation, bottom bar, offline banner, app startup.

`feature:*`:

- UI layer.
- Chứa route, screen, component, ViewModel, MVI contract, feature navigation.
- Không nên biết Retrofit, Room DAO, DataStore implementation.

`core:domain`:

- Chứa repository interface và use case.
- Không phụ thuộc implementation như Retrofit, Room, DataStore.

`core:data`:

- Chứa repository implementation.
- Kết nối network/database/datastore.
- Map DTO/entity sang domain model.

`core:network`:

- Retrofit service, request/response DTO, OkHttp interceptors/authenticator.

`core:database`:

- Room database, DAO, entity, schema.

`core:datastore`:

- Token/user/device storage, preferences.

`core:model`:

- Domain model thuần.
- Không nên chứa UI label hoặc Android resource.

`core:common`:

- `ApiResult`, `UiText`, `BaseViewModel`, `MviViewModel`, dispatcher binding.

`core:designsystem`:

- Theme, reusable Compose components, global dialog/loading primitives.

`core:testing`:

- Shared test utilities như `MainDispatcherRule`.

## Glossary Anh - Việt

`State`:

- Dữ liệu dùng để render UI.
- Nên là immutable `data class`.

`Event`:

- Hành động từ UI gửi vào ViewModel.
- Ví dụ: `SubmitLogin`, `SearchChanged`, `Refresh`.

`Effect`:

- Sự kiện dùng một lần.
- Ví dụ: navigate, show error, show snackbar/dialog.

`UseCase`:

- Business action ở domain layer.
- ViewModel gọi use case, không gọi repository implementation trực tiếp.

`Repository contract`:

- Interface trong `core:domain`.

`Repository implementation`:

- Class trong `core:data` implement contract.

`DTO`:

- Request/response model của API.
- Sống ở `core:network`, không leak lên UI.

`Entity`:

- Model của Room database.
- Sống ở `core:database`, không leak lên UI.

`Mapper`:

- Hàm/class chuyển DTO/entity sang domain model hoặc ngược lại.

`Source of truth`:

- Nguồn dữ liệu chính mà UI tin tưởng.
- Với offline-first, thường là Room cache qua repository.

`RemoteMediator`:

- Thành phần Paging 3 để sync network data vào local database.

`CompositionLocal`:

- Cơ chế truyền dependency/state xuống Compose tree.
- Project đang dùng `LocalGlobalAppState` cho global loading/dialog.

## Khi phân vân nên làm gì?

Nếu Đoàn đang sửa UI:

- Đọc `UI_GUIDELINES.md`.
- Xem màn hình gần nhất trong cùng feature.
- Giữ route/screen/content tách biệt.

Nếu Đoàn đang thêm logic business:

- Đọc `ARCHITECTURE.md`.
- Thêm use case ở `core:domain`.
- Implementation ở `core:data`.

Nếu Đoàn đang thêm API:

- DTO/service ở `core:network`.
- Repository implementation ở `core:data`.
- UI chỉ nhận domain model.

Nếu Đoàn đang cleanup:

- Đọc `ARCHITECTURE_DEBT.md`.
- Chỉ xử lý debt nhỏ, đúng phạm vi task.
- Không refactor lan rộng nếu chưa cần.

Nếu Đoàn đang viết test:

- Use case test ở `core:domain/src/test`.
- Repository test ở `core:data/src/test`.
- Flow test dùng Turbine.
- Coroutine test dùng `runTest`.

## Quy tắc quan trọng

- Không duplicate toàn bộ docs tiếng Anh sang tiếng Việt.
- Không để file này trở thành nguồn sự thật thứ hai.
- Khi docs tiếng Anh đổi lớn, cập nhật file này ở mức tóm tắt.
- Nếu Codex cần sửa code, đưa Codex đọc docs tiếng Anh trước.
- Nếu Đoàn cần hiểu nhanh, đọc file này trước.

## Đề xuất workflow cho Đoàn

Khi bắt đầu task mới:

1. Đọc phần liên quan trong file này.
2. Mở doc tiếng Anh tương ứng.
3. Xem code hiện tại trong module gần nhất.
4. Yêu cầu Codex code theo skill phù hợp:
   - `android-feature` khi thêm feature module.
   - `compose-screen` khi thêm Compose screen.
   - `viewmodel-state` khi sửa MVI/ViewModel.
   - `test-writing` khi viết test.
   - `code-review` khi review trước merge.

Đừng để docs làm Đoàn chậm lại. Dùng file này như bản đồ, còn code hiện tại là sự thật cuối cùng.
