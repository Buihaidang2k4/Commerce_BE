# Phân tích model SmartCommerce và schema không có khóa ngoại

Nguồn: tài liệu SmartCommerce 12 phần do người dùng cung cấp, đặc biệt mục V, VI và VII. Yêu cầu mới trong chat được ưu tiên: không dùng FK, JPA association, Java enum, DB enum, cascade, unique, check hoặc NOT NULL ngoài khóa chính. Tài liệu gốc đề xuất FK/unique và PostgreSQL; lần sửa này giữ MySQL hiện có để không đồng thời thay cả database engine và mô hình dữ liệu.

Kết quả: 55 entity độc lập, gồm 19 entity có sẵn được tái sử dụng, 3 entity mapping được tách từ join table cũ và 33 entity mới. Các tính năng mới được bổ sung ở mức model/repository/schema, chưa phải toàn bộ API, phân quyền và worker của marketplace đã được triển khai.

## 1. Những model tái sử dụng

| Model hiện có | Vai trò trong thiết kế mới | Thay đổi chính |
|---|---|---|
| User | Tài khoản Identity | Giữ ID, email, mật khẩu, trạng thái; bỏ roles/profile dạng object; role lưu qua UserRole |
| Role | Danh mục vai trò | Giữ roleName làm khóa chính để tương thích dữ liệu cũ; không tạo bảng roles mới song song |
| UserProfile | Hồ sơ khách hàng | userId là trường thường; bỏ danh sách address/cart/orders |
| Address | Sổ địa chỉ | profileId thường; type là String, mặc định HOME |
| Category | Cây danh mục | Thêm parentId, slug; bỏ collection products |
| Product | Sản phẩm thuộc shop | Thêm shopId, status, version; categoryId thường; bỏ images/inventory/reviews/cartItems/orderItems dạng object |
| Image | Ảnh sản phẩm | productId, variantId, objectKey, sortOrder; giữ bảng image và khả năng đọc ảnh cũ |
| Inventory | Tồn kho | productId, variantId thường; version và khóa dòng ở service; giữ bảng inventory thay vì thêm stock_items trùng chức năng |
| Cart | Giỏ hàng | profileId/userId thường; không lưu collection CartItem trong entity |
| CartItem | Sản phẩm trong giỏ | cartId/productId/variantId; số lượng và giá là cột thường |
| Order | Đơn cha | userId/profileId/paymentId/couponId; thêm orderNo, idempotencyKey, requestHash, currency, subtotal, version |
| OrderItem | Chi tiết đơn | orderId/sellerOrderId/productId/variantId; snapshot productTitle, sku, attributesSnapshot, price, discountAmount |
| OrderDeliveryAddress | Snapshot địa chỉ nhận | orderId thường; giữ bản sao địa chỉ lịch sử |
| OrderStatusHistory | Lịch sử trạng thái | orderId/changedByUserId thường; thêm fromStatus, reason; trạng thái là String |
| Payment | Payment intent tương thích luồng cũ | orderId thường; thêm provider/providerRef/currency; phương thức và trạng thái là String; giữ bảng payments |
| Coupon | Khuyến mãi | Thêm shopId; scope/discountType là String; danh sách áp dụng qua CouponCategory/CouponProduct |
| Review | Đánh giá sau mua | userId/profileId/productId/orderId/orderItemId thường; thêm status |
| PasswordResetOtp | OTP đổi mật khẩu | userId thường, không giữ object User |
| RevokedToken | Thu hồi token hiện có | Giữ bảng hiện có; bỏ unique ở DB; RefreshToken mới phục vụ lộ trình token rotation |

Giữ tên bảng/ID cũ giúp chuyển dữ liệu hiện có thay vì tạo cặp bảng cùng chức năng. UserResponse.id được đổi từ Integer sang Long để thống nhất BIGINT và tránh cắt ID lớn.

## 2. Bảng mapping độc lập

| Model | Bảng | Cột dữ liệu |
|---|---|---|
| UserRole | user_role | id, userId, roleName |
| CouponCategory | coupon_categories | id, couponId, categoryId |
| CouponProduct | coupon_products | id, couponId, productId |
| RolePermission | role_permissions | id, roleName, permissionId |
| ShopMember | shop_members | id, shopId, userId, role, status |

Ba bảng đầu tái sử dụng join table hiện có, thêm ID riêng và bỏ khóa chính kép/ràng buộc FK cũ khi migration. RolePermission và ShopMember là model mới. Các ID tham chiếu chỉ là giá trị vô hướng; không có annotation liên kết.

Không cần bảng mapping riêng cho mọi quan hệ một–nhiều. Ví dụ OrderItem.orderId đủ để truy vấn các item của một order. Mapping dành cho quan hệ nhiều–nhiều hoặc quan hệ có dữ liệu riêng như vai trò/thành viên.

## 3. Các model mới và giai đoạn áp dụng

| Module | Model mới | Mục tiêu và trạng thái |
|---|---|---|
| Identity/RBAC | RefreshToken, Permission, RolePermission | Token rotation, resource-action-scope; cần bổ sung service và kiểm quyền |
| Merchant | Shop, ShopMember | Đăng ký/duyệt shop, phân công seller/staff; cần API và tenant isolation |
| Catalog | ProductVariant | SKU, attributesJson, giá/trạng thái từng biến thể; cần nối luồng cart/stock/checkout theo variantId |
| Checkout | CheckoutQuote | Báo giá có TTL, snapshot giá/phí/giảm; cần quote API và revalidation |
| Inventory | StockReservation, StockMovement | Giữ hàng có hạn, xác nhận/giải phóng, nhật ký tồn; cần worker TTL và service idempotent |
| Orders | SellerOrder | Tách đơn theo shop, tổng tiền/trạng thái từng seller; cần triển khai phân nhóm checkout và seller API |
| Payment | PaymentTransaction, Refund | Ghi callback, đối soát, hoàn một phần; cần kiểm chữ ký, khóa/idempotency và workflow refund |
| Promotion | CouponRedemption | Theo dõi quota giữ/đã dùng/giải phóng theo user/order/shop |
| Shipping | Warehouse, Shipment, TrackingEvent | Kho, phân công shipper, tracking theo seller order |
| Return | ReturnRequest, ReturnItem | Xét duyệt trả hàng/toàn phần/một phần; Refund tham chiếu bằng returnRequestId |
| Event/Platform | OutboxEvent, ConsumerInbox, AuditLog, IdempotencyRecord | Event sau commit, chống xử lý lặp, audit và replay command |
| Notification | Notification, NotificationTemplate | Thông báo theo event, kênh, locale; cần sender/retry worker |
| Flash sale | FlashSaleCampaign, FlashSaleItem, FlashSalePurchase | Thời gian campaign, giá/quota, hạn mức user và TTL mua |
| AI/Search | AiConversation, AiMessage, SearchDocument, ProductEmbedding | Chat có căn cứ, dữ liệu truy xuất và chỉ mục/embedding dẫn xuất |
| Support | SupportTicket, Dispute | Ticket/khiếu nại, phân công hỗ trợ, giải quyết tranh chấp |

Các model flash sale, AI, support và quote được suy ra từ đặc tả chức năng, không phải đều có cột đầy đủ trong mục 7.2. Cấu trúc được bổ sung để giữ dữ liệu tối thiểu cho triển khai tiếp theo. ProductEmbedding chỉ giữ model, source hash và vectorReference; không giả định đã cài pgvector trong MySQL.

## 4. Đối chiếu 12 phần tài liệu

| Phần | Tác động đến model và backend |
|---|---|
| I Tổng quan | Multi-vendor yêu cầu Shop/SellerOrder/SKU; giao dịch giữ snapshot |
| II Công nghệ | Giữ Java 17/MySQL đang chạy; PostgreSQL/Java 21 là thay đổi độc lập, chưa tự nâng cấp |
| III Kiến trúc | Model chia theo ownership; OutboxEvent/ConsumerInbox giữ nền event; chưa tách microservice |
| IV Phân quyền | UserRole, Permission, RolePermission, ShopMember, Warehouse, AuditLog |
| V Chức năng | Bao phủ Identity/Merchant/Catalog/Cart/Order/Inventory/Payment/Promotion/Return/Flash/AI |
| VI Luồng | Order, Payment, StockReservation cần state machine và idempotency ở service |
| VII Database | Entity chỉ có scalar/ID; chỉ giữ PK và index thường; mô hình vật lý mới ưu tiên yêu cầu chat |
| VIII API | Giữ API hiện có; model mới chưa đồng nghĩa seller/quote/refund API đã có |
| IX Giao diện | Response DTO vẫn có thể gộp dữ liệu để FE hiển thị; entity DB không giữ object graph |
| X Phi chức năng | Cần kiểm thử đồng thời/tenant isolation; metadata test xác nhận không sinh FK |
| XI DevOps | Có script create/upgrade/verify MySQL; không tự chạy DDL lên database đang sử dụng |
| XII Kế hoạch | Hoàn tất chuyển persistence trước, sau đó mở rộng nghiệp vụ theo các giai đoạn dưới đây |

## 5. Những luồng hiện có đã chỉnh để bỏ cascade

- Đăng ký: lưu User → ghi UserRole → lưu UserProfile.userId → lưu Cart.profileId/userId. Không trông chờ User.save lưu toàn bộ graph.
- Sản phẩm: lưu Product.categoryId trước; khởi tạo Inventory.productId riêng; tạo dữ liệu mẫu cũng lưu inventory riêng.
- Giỏ hàng: CartItem được save/delete trực tiếp; tổng giỏ được tính lại từ query cartId; clear cart giữ bản ghi Cart và giải phóng lượng hàng đang giữ.
- Đơn hàng: lưu Order trước để có ID; lưu OrderItem snapshot với orderId; lưu snapshot địa chỉ; lưu Payment.orderId rồi cập nhật Order.paymentId. Mỗi luồng nằm trong transaction.
- Checkout từ cart: item được chuyển khỏi cart bằng lệnh xóa có kiểm cartId; không giải phóng lại tồn đã chuyển sang đơn.
- Mã giảm giá và role: thao tác bảng mapping rõ ràng, không dựa trên collection ManyToMany.
- Query: chuyển property-path association sang cột ID; join báo cáo vẫn được phép bằng điều kiện ID dù DB không có FK.
- Response: mapper kết hợp ModelLookup/repository để trả DTO có thông tin liên quan; không thêm object hoặc collection quan hệ ngược trở lại entity.
- Template hóa đơn/xuất báo cáo dùng snapshot tên sản phẩm thay vì gọi OrderItem.getProduct().
- Kho giữ khóa dòng và cập nhật trong transaction để không coi việc bỏ FK là bỏ kiểm soát đồng thời.

## 6. Quy ước model mới

Mỗi bảng có khóa chính để định danh. ID tham chiếu dùng Long hoặc String thường. Trạng thái lưu String và có constants tương ứng; không dùng Java enum hoặc native ENUM. Bảng mapping có ID riêng. Index trên ID tham chiếu là index thường, không unique. Version là cột số dùng optimistic locking khi cần, không phải ràng buộc FK.

Không xóa hoặc cập nhật dữ liệu liên quan ngầm. Service phải quyết định rõ xóa child/mapping nào và giữ snapshot giao dịch nào. Dữ liệu entity không có FK nghĩa là có thể lưu ID không tồn tại bằng SQL; API/service vẫn phải xác thực ID và quyền sở hữu trước khi xử lý nghiệp vụ.

## 7. Chuyển database hiện có

Các file trong db/mysql:

1. 01_create_scalar_schema.sql: tạo bảng còn thiếu; dùng được cho DB trống.
2. 02_upgrade_existing_schema.sql: gỡ FK/unique/check; thêm ID cho join table cũ; thêm cột còn thiếu; chuyển ENUM/SET sang VARCHAR; bỏ NOT NULL ngoài PK; thêm index thường; backfill payment.orderId, userId và tên sản phẩm snapshot.
3. 03_verify_scalar_schema.sql: kiểm tra không còn ràng buộc ngoài PK, không còn ENUM/SET/NOT NULL ngoài PK; liệt kê dữ liệu trùng hoặc orphan để đối soát.
4. model-schema.json: danh sách bảng/cột/index để kiểm tra và tái sinh SQL.

Chạy từ thư mục backend bằng MySQL client, chọn đúng DB và nhập mật khẩu tương tác. Không truyền password vào command history. Backup và dừng writer trước khi chạy trên dữ liệu hiện có. MySQL DDL có implicit commit, không dùng rollback SQL như cơ chế khôi phục; khôi phục bằng backup đã kiểm thử. Schema production dùng validate; cấu hình update không xóa các FK cũ, vì vậy cần migration thực tế.

Migration không đổi tên bảng, không xóa bản ghi và không tự xóa dữ liệu trùng/orphan. Dữ liệu mẫu hoặc DB đã tùy chỉnh tên/cột ngoài entity cũ cần được đối chiếu trước khi áp dụng. Cột liên kết cũ được giữ lại và chuyển thành cột thường.

## 8. Giới hạn và bước triển khai tiếp

Bỏ unique khiến kiểm tra exists trước save không đủ để đảm bảo không trùng khi có request đồng thời. Đối với tài khoản, SKU, callback provider_event_id, idempotency và mapping, cần khóa theo business key hoặc khóa bản ghi cha ổn định, cùng transaction/CAS. Việc tạo model ConsumerInbox/IdempotencyRecord chưa tự tạo cơ chế exactly-once. ModelLookup không thay thế authorization.

Luồng hiện có còn mang hành vi legacy: tồn kho chính theo product, giỏ hàng giữ tồn; payment hiện dùng số nguyên VND; trạng thái đơn legacy CREATED/PENDING được giữ cho FE. Các cột variantId, sellerOrderId và các model mới là nền chuyển dần sang SKU/multi-vendor; không được coi các tính năng mới đã hoàn tất.

Thứ tự đề xuất:

1. Merchant/RBAC: shop, membership, scope OWN_SHOP; thêm migration dữ liệu shop cho catalog hiện có.
2. SKU/Inventory: chuyển thao tác tồn kho sang variantId, reservation TTL và stock movement.
3. Quote/Checkout đa shop: quote có hạn, tính phí server, SellerOrder, snapshot SKU/attributes/giá đầy đủ.
4. Payment/Coupon: callback kiểm chữ ký/amount/currency, idempotency có khóa, quota và redemption.
5. Shipping/Return/Refund: tracking theo seller order, hoàn một phần và đối soát.
6. Outbox/Inbox/Audit/Notification: publisher/consumer và worker retry.
7. Flash sale/Search/AI: chỉ triển khai sau khi inventory/order chịu được test đồng thời.

Các ngưỡng hiệu năng, zero-oversell, replay callback và tenant isolation trong tài liệu cần integration/load tests riêng trước khi coi là đạt production.

## 9. Kết quả kiểm chứng trong phiên chỉnh sửa

- Biên dịch source và test thành công; 23 test chọn lọc đều qua, gồm 4 test persistence H2.
- Test tạo 55 bảng, khởi tạo toàn bộ repository/JPQL và xác nhận không có association JPA hay constraint ngoài PK.
- Kiểm tra lưu ID không tồn tại vẫn được DB chấp nhận; business service mới là nơi kiểm tra tính hợp lệ.
- Kiểm tra đăng ký, mapping coupon, giỏ hàng, checkout, snapshot, địa chỉ và payment được lưu riêng mà không cần cascade.
- Test trên H2 không thay thế MySQL migration test; Docker/MySQL chưa được truy cập trong phiên nên SQL chưa được chạy lên MySQL thực tế.
- Không chạy test contextLoads cần môi trường dịch vụ thật; không thay đổi dữ liệu database đang sử dụng.

Các endpoint giỏ hàng trả CartResponse (items) thay cho entity Cart thô để vẫn gộp được dữ liệu items sau khi entity không còn collection. Client từng đọc cartItems từ endpoint trả entity cần chuyển sang items.
