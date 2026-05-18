Chạy Spotless check + Detekt:

```bash
./scripts/check-lint.sh
```

Nếu fail, report:
- Check nào fail (Spotless hay Detekt).
- Exact file paths và rule names từ output.
- Spotless violation → auto-fixable bằng `/format` (`./gradlew spotlessApply`).
- Detekt violation → cần fix code thủ công.

Nếu `$ARGUMENTS` là module path (ví dụ: `feature:tasks`), chạy narrow hơn:

```bash
./gradlew :feature:tasks:spotlessCheck :feature:tasks:detekt
```

Thay `feature:tasks` bằng module được cung cấp trong arguments.
