Read `docs/ARCHITECTURE_DEBT.md` in full.

Task: $ARGUMENTS
(Để trống = triage summary; hoặc mô tả item mới cần document)

---

**Nếu không có arguments — triage mode:**

1. List tất cả OPEN (non-Resolved) items, group by priority (Medium trước, Low sau).
2. Mỗi item hiển thị: module, issue summary, target solution summary, priority, status.
3. Chạy `git diff --stat` để identify open items liên quan đến code đang thay đổi.
4. Suggest 1 item cao giá trị nhất nên tackle tiếp dựa trên blast radius và working context hiện tại.

---

**Nếu có description — draft mode:**

1. Draft entry mới theo đúng format trong `docs/ARCHITECTURE_DEBT.md`:
   - H2 heading mô tả rõ issue
   - **Location(s)**: module/file liên quan
   - **Issue**: mô tả vấn đề
   - **Impact**: ảnh hưởng nếu không fix
   - **Target solution**: hướng giải quyết
   - **Priority**: Low / Medium / High
   - **Status**: Deferred

2. Present draft để review.

**KHÔNG tự write vào file — chờ user confirm trước khi edit `docs/ARCHITECTURE_DEBT.md`.**
