# Schema MySQL với tham chiếu ID thường

Áp dụng cho MySQL 8.0.16 trở lên, giữ engine hiện có của backend. Chỉ có PK và index không unique; không có FK, CHECK, UNIQUE, ENUM, SET hoặc NOT NULL ngoài PK.

Chạy từ thư mục backend, sau khi backup/restore thử và dừng writer:

```text
mysql -h <host> -u <migration-user> -p <database>
SOURCE db/mysql/01_create_scalar_schema.sql;
SOURCE db/mysql/02_upgrade_existing_schema.sql;
SOURCE db/mysql/03_verify_scalar_schema.sql;
```

DB mới dùng file 01; chạy thêm 02 an toàn để backfill và kiểm tra đồng nhất. File 02 dành cho schema theo entity cũ của project, có thể chạy lại. Không chạy vào DB có cùng tên bảng nhưng thuộc ứng dụng khác.

File 02 không xóa bảng/hàng dữ liệu. Nó thay đổi constraint và kiểu cột, thêm surrogate id cho user_role/coupon_categories/coupon_products, thêm cột và index còn thiếu. MySQL DDL implicit commit: cần backup, không trông chờ ROLLBACK.

Script chưa được áp dụng lên DB đang chạy và chưa được kiểm thử trên MySQL thực tế trong phiên này. Test H2 xác nhận schema do JPA sinh ra, query và luồng lưu; không thay thế kiểm thử migration trên bản sao MySQL.

Sau migration, ba truy vấn đầu của file 03 phải trả về rỗng. Các truy vấn chẩn đoán trùng/orphan cần được đối soát ở tầng nghiệp vụ, không tự xóa dữ liệu.

Tái sinh SQL từ source entity: node scripts/generate-scalar-schema.cjs.
Xem docs/SMARTCOMMERCE_MODEL_ANALYSIS.md để biết model nào được tái sử dụng và những tính năng mới chưa có service/API.
