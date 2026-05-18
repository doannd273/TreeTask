Chạy full local CI suite:

```bash
./scripts/run-ci.sh
```

Pipeline: `spotlessCheck` → `detekt` → `testDebugUnitTest` → `assembleDebug`.

Nếu pass hết, report rõ ràng là xanh.

Nếu fail, report:
- Step nào fail.
- Relevant error lines (file:line, test name, assertion).
- Suggested fix:
  - Spotless failure → chạy `/format` hoặc `./gradlew spotlessApply`, rồi recheck.
  - Detekt failure → inspect file:rule được flag, fix thủ công trước khi recheck.
  - Test failure → show test name, assertion message, stack trace.
  - Build failure → show compiler error với file:line reference.

Không commit hay push.
