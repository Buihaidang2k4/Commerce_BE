# Chuẩn tên model và chuyển dữ liệu

Đã đồng bộ 55 entity cùng repository, service, mapper và truy vấn liên quan. Danh sách đầy đủ tên bảng, trường và kiểu trước/sau nằm trong `MODEL_NAMING_MANIFEST.json`.

## Quy ước

- Class dùng danh từ số ít PascalCase; bảng dùng số nhiều snake_case; thuộc tính Java dùng camelCase.
- Khóa chính thống nhất `Long id`; tham chiếu dùng `<đối tượng>Id`, lưu scalar, không tạo FK hay quan hệ JPA. Mapping nhiều–nhiều vẫn là bảng riêng.
- Tên nghiệp vụ cụ thể: `UserAccount`, `ShoppingCart`, `StockBalance`, `ShopOrder`, `PaymentIntent`, `ProductImage`, `ProductReview`.
- Tiền dùng tên `…Amount`, mã tiền tệ `currencyCode`; số lượng dùng `…Quantity`; snapshot thể hiện rõ hậu tố `Snapshot`.
- Audit kế thừa `CreatedEntity`/`AuditableEntity`: `createdAt`, `createdBy`, `updatedAt`, `updatedBy`. Lifecycle JPA tự điền; thao tác bulk/native SQL cần tự cập nhật audit. Người thực hiện là `principal:<tên đăng nhập>` hoặc `system`.
- Mốc thời gian dùng `Instant`, lưu UTC; ngày thuần dùng `LocalDate`. Trạng thái vẫn là String, không dùng enum.

`Role` có `id` số, `code` và `name`; bảng gán vai trò dùng `roleId`. Endpoint vai trò hiện có vẫn nhận code. DTO tiếp tục có mapper tường minh. Theo yêu cầu mới, entity đã bỏ `@JsonProperty` và `@JsonAlias`: endpoint đọc/trả entity trực tiếp dùng tên trường Java hiện tại, không còn nhận alias JSON cũ. Endpoint dùng DTO vẫn theo tên trường của DTO.

`StockBalance.onHandQuantity` là tổng hàng còn trong kho; `reservedQuantity` là phần đang giữ. Lượng có thể bán được tính bằng hiệu hai trường này. Giữ hàng tăng reserved, xác nhận trừ cả hai, hủy giữ chỉ giảm reserved. `PaymentIntent.amount` đã thống nhất `BigDecimal`; quy đổi đơn vị nhà cung cấp nằm tại adapter thanh toán.

## Database mới

Chạy `database/01_canonical_schema.sql` trên MySQL 8 trước khi khởi động. Mặc định Hibernate dùng `validate`. Schema gồm PK và index thường, không FK, UNIQUE hoặc CHECK.

## Database đã có dữ liệu

1. Dừng các tiến trình ghi, sao lưu và thử khôi phục trên database riêng trước.
2. Đối chiếu manifest với schema thực tế. Script dựa trên model cũ trong project, chưa chạy trên MySQL của bạn. Nếu cả tên bảng cũ và mới cùng tồn tại, script dừng để đối soát.
3. Trong cùng phiên MySQL, đặt `SET @legacy_time_zone = '+07:00';` nếu DATETIME cũ lưu giờ Việt Nam, hoặc `'+00:00'` nếu đã lưu UTC. Phải xác định theo dữ liệu thực tế.
4. Chạy `database/02_rename_existing_models.sql` **trước** `01_canonical_schema.sql`. Sau đó chạy file 01 để tạo những bảng còn thiếu. Không dùng chế độ tiếp tục khi SQL lỗi.
5. Kiểm tra số dòng, ánh xạ vai trò, tổng tiền, audit và tồn kho rồi khởi động backend với `DB_SCHEMA_MODE=validate`.

Migration đổi tên bảng/cột, chuyển Role sang ID số, bỏ FK/UNIQUE/CHECK và chuyển timestamp cũ sang UTC. Bảng kỹ thuật `schema_migration_steps` ghi các bước chuyển dữ liệu đã chạy. DDL MySQL tự commit; bản sao lưu là phương án khôi phục. Cột legacy được giữ để đối soát; chưa tự xóa dữ liệu cũ. Các index cũ có thể vẫn giữ tên cũ trên DB đã tồn tại; không ảnh hưởng ánh xạ.

Đợt này tập trung chuẩn tên. Những thay đổi nghiệp vụ trong bản review như bỏ trường sở hữu trùng, chuyển toàn bộ giá sang SKU, bỏ ảnh Blob, hoặc hash token/OTP chưa thực hiện tự động. Các trường chuyển tiếp và hợp đồng API cũ vẫn được giữ.

## Kiểm chứng

Build Java 17 bằng Maven offline; kiểm tra tạm trên H2 khởi tạo đủ 55 entity và 55 repository, không FK/UNIQUE/CHECK, kiểm tra mapping vai trò, audit, DTO và phép tính giữ/xuất kho. Không tạo lại bộ file test đã xóa. Kiểm tra H2 không thay thế việc chạy migration trên bản sao MySQL thực tế.
