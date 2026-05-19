# UI Guidelines

TreeTask uses Jetpack Compose + Material 3 with a shared design system in `core:designsystem`. UI should be clear, practical, easy to scan, and appropriate for a task management app.

## Principles

- Screens should go directly to the primary workflow and should not behave like landing pages.
- Information hierarchy should be clear: title, filters/search/actions, list/content.
- Feature screens should not create their own global theme.
- Layout should be stable across screen sizes and should not rely too heavily on magic numbers.
- Loading/error/empty states need clear handling.
- User-facing text must use string resources.

## Compose Structure

Route:

- Obtains the ViewModel.
- Collects state/effects in a lifecycle-aware way.
- Calls navigation callbacks.
- Bridges global loading/dialog state.
- For success/error dialogs that navigate after dismissal, sends an acknowledgement event to the ViewModel; the ViewModel then emits the navigation effect.

Screen/Content:

- Receives state and callbacks.
- Does not inject dependencies.
- Does not know about `NavController`.
- Is easy to preview with fake state.
- Is `internal` when it is not a feature entry point.

Components:

- Stateless, or with clear state hoisting.
- Callback names describe actions: `onTaskClick`, `onSearchChange`, `onClearClick`.
- Avoid calling ViewModel directly from small components when a callback can be passed instead.
- Generic reusable components belong in `core:designsystem`; keep feature-local components only when they are truly feature-specific.

## Design System

Use from `core:designsystem`:

- `TreeTaskTheme`
- `Color.kt`, `Type.kt`, `Shape.kt`
- Dialog/loading components used by app global state
- Reusable components such as `LinkText`, `DebouncedClick`, `OtpInput`, `EmailInput`, `PasswordInput`, and `CommonHeader`
- Debug overlay wrapper when needed

Avoid:

- Copying colors/typography between features.
- Creating global components inside feature modules.
- Sharing generic UI by importing components across feature subpackages.
- Reading feature string resources from generic `core:designsystem` components; pass labels/copy as `String` parameters from the caller.
- Adding new app-level composition locals inside features.

## Material 3 Usage

- Use `Scaffold` for screens with top/bottom/fab/snackbar-like surfaces.
- Use `FloatingActionButton` for primary creation actions such as adding a task.
- Use `FilterChip`/segmented controls for status filters.
- Use `OutlinedTextField`/search fields for input.
- Use `LazyColumn`/`LazyVerticalGrid` for lists.
- Use `CircularProgressIndicator` for inline loading.
- Use global dialogs for errors/blocking messages following the current pattern.

## State Handling

Each screen should have:

- Loading state.
- Error effect or inline error depending on UX.
- Empty state when a list can be empty.
- Retry action when network/paging load fails.
- Disabled state for actions while submitting.

Paging:

- Handle `LoadState.Loading` for refresh/append.
- Handle `LoadState.Error` with retry.
- Key items by stable id.
- Use `itemContentType` when a list has many items of the same type.

## Text and Localization

- All real text should live in `res/values/strings.xml`.
- Add `res/values-vi/strings.xml` when the screen has Vietnamese translations.
- Do not hardcode text in ViewModels.
- Preview/mock data may be hardcoded when it is not product copy.
- `UiText.DynamicString` should be used only for server messages or runtime data.

## Accessibility

- Action icons need meaningful `contentDescription`.
- Decorative icons may use `contentDescription = null`.
- Button labels should describe the action.
- Do not rely on color alone to express state.
- Tap targets should be large enough according to Material guidance.

## Layout

- Use `WindowInsets.safeDrawing` when a screen needs to avoid system bars.
- Use consistent spacing: 4, 8, 12, 16, 24 dp.
- Do not place important UI too close to screen edges.
- Text inside cards/buttons must not overflow or overlap other content.
- For list items, keep height/spacing stable for smooth scrolling.
- Add at least one happy-path preview; add empty/error previews when layout is complex.

## Navigation UI

- Bottom bar is only for top-level destinations and is managed by `app`.
- Feature modules should not render their own bottom bar.
- Auth/splash/start destination is decided by the app/root layer.
- Features expose graph/navigation helpers; the app wires them together.
- Dialog dismissal should not hide navigation decisions in Route lambdas; send an event and let the ViewModel emit an effect.

## Theming and Resources

- Drawables/vectors live in the module that uses them.
- Resource names always use the module prefix.
- Use direct colors only when very local and justified; prefer theme tokens.
- If an icon is reused across features, consider moving it to `core:designsystem`.

## Review Checklist

- Screen has separated route/screen/content layers.
- State/effects are collected lifecycle-aware.
- Loading/error/empty paths are handled.
- Text uses resources.
- Action icons have accessibility labels.
- Preview builds.
- No unnecessary UI dependencies are added.
- Business logic is not placed in composables.
