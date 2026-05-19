Apply Spotless auto-formatting:

```bash
./scripts/apply-spotless.sh
```

Sau đó verify không còn violation:

```bash
./gradlew spotlessCheck
```

Nếu `$ARGUMENTS` là module path cụ thể (ví dụ: `feature:tasks`), chạy narrow:

```bash
./gradlew :<module>:spotlessApply
./gradlew :<module>:spotlessCheck
```

Thay `<module>` bằng giá trị trong arguments.

Lưu ý: Spotless chỉ fix formatting (indentation, trailing whitespace, import order). Detekt violations cần fix thủ công — dùng `/lint` để check.
