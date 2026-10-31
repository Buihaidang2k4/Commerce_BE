> Cập nhật: phần chuẩn tên đã triển khai. Xem [kết quả và migration](MODEL_NAMING_MIGRATION.md). Các đề xuất nghiệp vụ dưới đây không đồng nghĩa đã được thực hiện toàn bộ.

# Phân tích chuẩn hóa entity SmartCommerce

Đối chiếu source ngày 31/10/2026: **55 entity, 806 trường khai báo**. Tài liệu đưa ra tên đích, trường cần thêm/bỏ và điều kiện chuyển đổi. Đây là phương án thiết kế; chưa đổi entity, API hoặc database. Quy ước được chọn để thống nhất project, không phải tiêu chuẩn duy nhất cho mọi hệ thống.

Tiếp tục giữ yêu cầu hiện tại: ID tham chiếu là cột thường; không thêm FK, JPA association, enum hoặc constraint DB. Không tạo lại file test đã được yêu cầu xóa.

## 1. Kết luận quan trọng

| Phát hiện từ source hiện tại | Đánh giá |
|---|---|
| 55/55 entity thêm activeStatus/createDate/updateDate/createBy/updateBy | Audit đang được lặp lại và áp dụng cả cho mapping, nhật ký và giao dịch |
| 41 entity có createdAt hoặc createAt bên cạnh createDate | Có hai nguồn biểu diễn thời gian tạo |
| 39 entity có updatedAt hoặc updateAt bên cạnh updateDate | Có hai nguồn biểu diễn thời gian cập nhật |
| 28 entity có cả status và activeStatus | Cần phân biệt trạng thái nghiệp vụ và trạng thái bản ghi; không dùng hai cờ cùng nghĩa |
| Không entity nào còn initializer trực tiếp ở field | Không tự nó là lỗi, nhưng các service hiện vẫn tính toán với Integer/Double có thể null |
| User có enabled/status/activeStatus; Review có status/deleted/activeStatus | Phải định nghĩa vai trò từng trường trước khi loại bỏ |
| Giao dịch lưu đồng thời userId/profileId; Product và SKU cùng giữ giá | Chưa chốt nguồn dữ liệu chính, dễ phát sinh giá trị không khớp |
| Order.paymentId và Payment.orderId tham chiếu hai chiều | Cần chọn một hướng chính, tránh phải cập nhật hai cột độc lập |
| Payment.amount dùng Long, các bảng transaction/refund dùng BigDecimal | Cần thống nhất kiểu tiền và đơn vị |

Ưu tiên sửa audit, trạng thái và nguồn dữ liệu trước khi đổi hàng loạt tên bảng. Không cần thêm nhiều entity ngay; phần lớn vấn đề là ý nghĩa và vòng đời dữ liệu trong 55 bảng hiện có.

## 2. Quy ước đặt tên đích

| Thành phần | Quy ước | Ví dụ |
|---|---|---|
| Entity | Danh từ số ít, PascalCase | UserAccount, ProductVariant, PaymentIntent |
| Bảng | Danh từ số nhiều, snake_case | users, product_variants, payment_intents |
| Khóa chính | id, kiểu Long cho entity nội bộ | Order.id; DTO/API vẫn có thể dùng orderId |
| ID tham chiếu | Tên đối tượng + Id | userId, shopOrderId, variantId; đều là trường thường |
| Mã nghiệp vụ | Code | currencyCode, carrierCode, memberRoleCode |
| Số hiển thị | Number | orderNumber, trackingNumber, ticketNumber |
| Tên hiển thị | name/displayName | Product.name, UserProfile.displayName |
| Tiền | Amount, BigDecimal | subtotalAmount, shippingAmount, totalAmount |
| Đơn giá | Price | unitPrice, salePrice |
| Số lượng / số lần | Quantity / Count | reservedQuantity / reviewCount, attemptCount |
| Tỷ lệ | Percentage hoặc Rate, có quy ước giá trị | discountPercentage dùng khoảng 0–100 |
| Thời điểm | Động từ đúng thì + At | createdAt, startsAt, expiresAt, consumedAt |
| Snapshot | Hậu tố Snapshot | productNameSnapshot, skuSnapshot |
| Boolean | Tên property rõ nghĩa, tránh tiền tố is | enabled, defaultShippingAddress |

Không phải đổi tên tất cả entity mới là tốt. Product, Category, Order, Shipment, Coupon đã rõ nghĩa thì giữ. Đổi các tên mơ hồ như Image, Inventory hoặc tên thể hiện sai vai trò như FlashSalePurchase khi bản ghi mới chỉ giữ suất.

PK `id` là quy ước model đích. Có thể đổi tên property Java và giữ cột cũ bằng `@Column(name=...)` trước khi đổi schema vật lý. Role hiện có PK String là trường hợp chuyển cấu trúc ID, không được thay kiểu trực tiếp.

## 3. Audit và trạng thái dùng chung

### Bảng có dữ liệu được cập nhật

Chọn **một** bộ audit:

| Trường đích | Kiểu | Ý nghĩa |
|---|---|---|
| createdAt | Instant | Thời điểm tạo |
| updatedAt | Instant | Thời điểm cập nhật |
| createdBy | String | Actor key ổn định như user:10 hoặc system:reservation-expiry |
| updatedBy | String | Actor key thực hiện cập nhật |

`createDate → createdAt`, `updateDate → updatedAt`, `createBy → createdBy`, `updateBy → updatedBy`. Không dùng email hoặc tên hiển thị có thể đổi làm danh tính audit.

Có thể dùng `AuditableEntity` với `@MappedSuperclass` để chia sẻ trường. Đây là kế thừa field, không phải quan hệ JPA và không sinh FK. Cần có auditing/lifecycle/service thực sự ghi dữ liệu; `@JsonFormat` không tự cập nhật thời gian.

Nếu hai cột audit cũ đã có dữ liệu khác nhau, phải kiểm tra nguồn ghi để chọn giá trị. Không tự COALESCE rồi xóa cột còn lại.

### Bảng lịch sử và hàng đợi

- AuditEvent, StockMovement, OrderStatusTransition và ShipmentTrackingEvent nên ghi thêm; sửa sai bằng bản ghi bù. Không áp activeStatus/updatedBy như bảng danh mục.
- `occurredAt` là lúc sự kiện xảy ra; `receivedAt/createdAt` có thể là lúc tiếp nhận/lưu. Giữ cả hai chỉ khi khác biệt này cần thiết.
- Outbox/Inbox không hoàn toàn bất biến: payload/event cần ổn định nhưng trạng thái xử lý, số lần thử và lease có thể cập nhật.
- Bảng mapping đơn giản có thể chỉ cần audit tối thiểu. Nếu cần lịch sử thu hồi, dùng revokedAt thay vì xóa vật lý hoặc một cờ active không rõ nghĩa.

### Các cờ trạng thái

- `status`: vòng đời nghiệp vụ. Dùng tên cụ thể khi một bảng có nhiều máy trạng thái, ví dụ accountStatus/moderationStatus.
- `enabled`: công tắc cấu hình, phù hợp với Coupon hoặc NotificationTemplate; không thêm status cùng nghĩa.
- `deletedAt/archivedAt`: chỉ thêm khi có yêu cầu xóa mềm/lưu trữ, không áp mọi bảng giao dịch.
- `activeStatus`: đề xuất bỏ khỏi các bảng không có nhu cầu riêng. Nếu thực sự là trạng thái kỹ thuật độc lập, đổi thành recordStatus, định nghĩa constants số và mô tả nó khác status như thế nào.
- `@Version`: giữ null khi tạo entity mới; không khởi tạo cứng 0 bằng builder vì Spring Data có thể đánh giá sai trạng thái mới/cũ.

## 4. Các thay đổi không được rename máy móc

| Hiện tại | Đích | Điều kiện chuyển đổi |
|---|---|---|
| profileId trên giao dịch | userId | Backfill qua UserProfile.userId; kiểm tra trường hợp hai ID đang mâu thuẫn |
| Role.roleName PK String | id Long + code + name | Gán ID, backfill roleId ở UserRole/RolePermission, giữ authority code của JWT |
| Inventory.available | onHandQuantity | Theo luồng hiện tại: onHand = available + reserved. Sửa mọi phép giữ/xuất/hủy, không rename available thành onHand |
| User.password | passwordHash | Rename tên mô tả hash; không băm lại hash đang tồn tại |
| OTP thô | otpHash | Đổi cả sinh/xác minh, cách hash và giới hạn thử; không chỉ thay tên cột |
| JWT thô trong blacklist | tokenId hoặc tokenHash | Đổi thuật toán tra cứu; xử lý token cũ đến hết TTL hoặc backfill có kiểm chứng |
| Payment.amount Long | BigDecimal amount | Giữ đơn vị VND hiện tại; chuyển đổi provider nằm trong adapter, không nhân/chia tiền khi đổi kiểu |
| Payment.status legacy | providerStatus/providerResponseCode | Chuyển sang dữ liệu provider; PaymentIntent chỉ giữ một trạng thái nghiệp vụ |
| Order.paymentId và Payment.orderId | PaymentIntent.orderId | Chốt hướng truy vấn và trường hợp nhiều lần thử trước khi bỏ cột |
| Double kích thước không rõ đơn vị | weightGrams, lengthMm… | Xác minh đơn vị ở FE/dữ liệu cũ trước khi chuyển giá trị |
| Image Blob/url/downloadUrl/objectKey | Object storage reference | Upload/backfill object, xác minh đọc ảnh rồi mới bỏ Blob/URL cũ |

## 5. Tên đích và trường cần chỉnh cho đủ 55 entity

Các hàng sau đều áp dụng quy tắc audit ở trên. “Bỏ” là bỏ sau khi code/dữ liệu không còn phụ thuộc. Trường thêm có điều kiện không bắt buộc phải có tại MVP.

| Entity hiện tại → đích | Bảng đích | Đổi tên / thêm / bỏ và lý do |
|---|---|---|
| User → **UserAccount** | users | password → passwordHash; status → accountStatus; lockedReason → lockReason. Thêm emailVerifiedAt/lastLoginAt. Bỏ enabled khi Spring Security đã suy ra từ accountStatus; không giữ thêm activeStatus cùng nghĩa. |
| UserProfile → **UserProfile** | user_profiles | profileId → id; username → displayName; mobileNumber → phoneNumber. gender Boolean → genderCode String nếu cần nhiều hơn hai giá trị/không cung cấp. avatarObjectKey chỉ thêm khi có ảnh đại diện. Không sao chép email/quyền từ User. |
| Role → **Role** | roles | Thêm id Long và name hiển thị; roleName hiện thực chất là mã USER/ADMIN nên đổi thành code. Cần migration ID, không rename PK String sang Long. |
| UserRole → **UserRole** | user_roles | roleName → roleId sau khi Role có ID số; userId giữ nguyên. assignedAt/assignedBy hoặc revokedAt chỉ khi cần lịch sử cấp/thu hồi quyền. Không thêm collection role vào UserAccount. |
| Permission → **Permission** | permissions | resource/action/scope → resourceCode/actionCode/scopeCode. Giữ code ổn định, ví dụ PRODUCT:UPDATE:OWN_SHOP; chốt quy tắc khớp code với ba thành phần. Đề xuất giữ scope tại Permission, không lặp scope ở cả assignment khi chưa có luật override. |
| RolePermission → **RolePermission** | role_permissions | roleName → roleId; permissionId giữ nguyên. Không cần thêm trường nghiệp vụ. Audit tối thiểu hoặc revokedAt nếu phải giữ lịch sử; không copy trạng thái thừa. |
| RefreshToken → **RefreshSession** | refresh_sessions | familyId → tokenFamilyId; replacedByTokenId → replacedBySessionId. Thêm lastUsedAt/revokeReason; userAgent/ipAddress chỉ khi quản lý thiết bị. Tiếp tục lưu tokenHash, không thêm token thô. |
| RevokedToken → **TokenRevocation** | token_revocations | token → tokenId theo JWT jti hoặc tokenHash nếu không dùng jti. Thêm userId/tokenType/revokeReason. Không đổi tên thành tokenHash nhưng giữ nguyên JWT; phải chuyển cách ghi/tra cứu. |
| PasswordResetOtp → **PasswordResetChallenge** | password_reset_challenges | otp → otpHash; expiredAt → expiresAt; used → consumedAt. Thêm failedAttemptCount/maxAttempts/lastAttemptAt. Mã ngắn cần hash có bí mật phía server hoặc password hashing phù hợp và giới hạn thử; không chỉ hash nhanh không khóa. |
| Address → **UserAddress** | user_addresses | addressId → id; profileId → userId; fullName/phone → recipientName/recipientPhone; type → addressType; additionalInfo → deliveryInstructions; isDefault → defaultShippingAddress. districtID/provinceID → districtCode/provinceCode. Thêm countryCode; mã ngoài hệ thống nên là String, tên địa giới thêm hậu tố Name. |
| Shop → **Shop** | shops | Giữ name/slug/status. Thêm contactEmail/contactPhone nếu có liên hệ gian hàng; shopCode chỉ nếu cần mã nội bộ. ownerUserId và approvedByUserId có hai vai trò khác nhau; giữ approvedAt riêng. |
| ShopMember → **ShopMembership** | shop_memberships | role → memberRoleCode, khác Role toàn hệ thống. Thêm joinedAt/leftAt; invitedByUserId nếu có mời nhân viên. status thể hiện INVITED/ACTIVE/SUSPENDED/LEFT; không thêm activeStatus cùng nghĩa. |
| Warehouse → **Warehouse** | warehouses | Thêm warehouseCode/contactName/contactPhone khi vận hành kho. addressId hiện có thể trỏ sổ địa chỉ khách, nên đề xuất địa chỉ kho độc lập: addressLine1, wardName, districtName, provinceName, countryCode. Bỏ addressId sau khi backfill nếu không có bảng address chung với ownership rõ. |
| Category → **Category** | categories | categoryId → id; categoryName → name; createAt/updateAt → audit chuẩn. Thêm sortOrder; categoryCode chỉ nếu có mã danh mục nghiệp vụ. Giữ parentId là trường thường; không lưu danh sách products/children. |
| Product → **Product** | products | productId → id; productName → name; bio → shortDescription; avgRating → averageRating; soldCount → soldQuantity. Thêm publishedAt. Sau khi chuyển đầy đủ SKU, bỏ giá và kích thước cấp Product nếu thực tế thuộc từng SKU. Counter là dữ liệu tổng hợp. Không cấm trùng tên sản phẩm toàn marketplace chỉ để nhận diện sản phẩm. |
| ProductVariant → **ProductVariant** | product_variants | specialPrice → salePrice; currency → currencyCode. Thêm barcode khi quét mã; weightGrams/lengthMm/widthMm/heightMm khi vận chuyển theo SKU. Chốt SKU là nguồn giá; mỗi Product có default SKU khi chuyển luồng cũ. Không tự đoán đơn vị của dữ liệu Double hiện có. |
| Image → **ProductImage** | product_images | fileType → contentType. Thêm sizeBytes/altText khi cần. Object storage dùng objectKey và bucketName nếu nhiều bucket. Sau backfill, bỏ Blob và URL ký tạm/URL backend cố định. Giữ productId là chủ sở hữu, variantId tùy chọn giới hạn ảnh cho SKU. |
| Cart → **ShoppingCart** | shopping_carts | cartId → id; totalPrice → subtotalAmount. Thêm currencyCode. Bỏ profileId, giữ userId. guestTokenHash/expiresAt chỉ khi hỗ trợ giỏ khách. Tổng tiền giỏ là cache và không gồm mọi phí checkout. |
| CartItem → **CartItem** | cart_items | cartItemId → id; totalPrice → lineAmount. Sau khi chuyển SKU, productId có thể bỏ nếu hoàn toàn suy ra từ variantId. unitPrice/lineAmount là cache, không phải cam kết giá cuối cùng. selected chỉ thêm khi cần lưu lựa chọn dòng. |
| CheckoutQuote → **CheckoutQuote** | checkout_quotes | subtotal/discountTotal/shippingTotal/grandTotal → subtotalAmount/discountAmount/shippingAmount/totalAmount; currency → currencyCode. Thêm requestHash và consumedAt hoặc orderId để biết quote đã dùng. Snapshot theo shop đặt trong snapshotJson; chỉ tách quote item khi cần query riêng. |
| Inventory → **StockBalance** | stock_balances | inventoryId → id. Chuyển nghĩa sang onHandQuantity/reservedQuantity, availableQuantity tính ra. Thêm warehouseId khi đa kho. Bỏ productId sau khi variantId là nguồn chuẩn nếu không cần projection. Không lưu ba số dư có thể ghi độc lập. Giữ version. |
| StockReservation → **StockReservation** | stock_reservations | quantity → reservedQuantity. Thêm orderItemId hoặc reservationKey theo allocation; confirmedAt/releasedAt/releaseReason khi cần. expiresAt là hạn dự kiến, expiredAt là lúc thực tế hết hạn nếu ghi nhận. Phải xác định chủ giữ là cart hay order khi còn giữ tồn từ cart. |
| StockMovement → **StockMovement** | stock_movements | refType/refId → referenceType/referenceId; actorId → actorUserId nếu chỉ là tài khoản. Thêm movementType/occurredAt/warehouseId theo nghiệp vụ. quantityDelta phải rõ delta on-hand hay reserved. Không sửa/xóa mềm nhật ký để điều chỉnh kho; ghi movement bù. |
| Order → **Order** | orders | orderId → id; orderNo → orderNumber; orderStatus → status; subtotal/shippingFee → subtotalAmount/shippingAmount; currency → currencyCode. Thêm placedAt và mốc trạng thái cần thiết. Bỏ profileId; bỏ paymentId sau khi PaymentIntent.orderId là hướng chính. orderDate chỉ giữ nếu có nghĩa business date riêng. |
| SellerOrder → **ShopOrder** | shop_orders | Tên ShopOrder khớp shopId: một seller có thể có nhiều shop. Đổi tham chiếu sellerOrderId → shopOrderId. subtotal/shippingFee/discountTotal/grandTotal → subtotalAmount/shippingAmount/discountAmount/totalAmount. Thêm shopOrderNumber/currencyCode. |
| OrderItem → **OrderItem** | order_items | orderItemId → id; sellerOrderId → shopOrderId; productTitle → productNameSnapshot; sku → skuSnapshot; attributesSnapshot → variantAttributesSnapshot; price → unitPrice. Thêm totalAmount/currencyCode, taxAmount chỉ khi có thuế. Giữ productId/variantId và snapshot lịch sử; cartItemId có thể bỏ sau khi chuyển giỏ an toàn. |
| OrderDeliveryAddress → **OrderShippingAddress** | order_shipping_addresses | deliveryNote → deliveryInstructions; thêm countryCode và mã địa giới khi cần gửi carrier. Không dùng địa chỉ cá nhân thay cho snapshot. Chỉ thêm shopOrderId nếu mỗi shop được giao một địa chỉ khác nhau. |
| OrderStatusHistory → **OrderStatusTransition** | order_status_transitions | historyId → id; orderStatus → toStatus; changedAt → occurredAt; changedByUserId → actorUserId. Giữ fromStatus/reason; thêm correlationId/source khi cần. Nhật ký ghi thêm, không activeStatus hoặc audit người sửa. |
| Payment → **PaymentIntent** | payment_intents | paymentId → id; amount Long → BigDecimal; paymentMethod → methodCode; paymentStatus → status; currency → currencyCode; expiredAt → expiresAt. Thêm paidAt/version. Bỏ status legacy thứ hai; chuyển dữ liệu VNPay/provider theo lần giao dịch sang PaymentTransaction hoặc adapter details. Giữ orderId là hướng tham chiếu chính. |
| PaymentTransaction → **PaymentTransaction** | payment_transactions | paymentId → paymentIntentId; provider → providerCode; providerRef → providerTransactionId; status → transactionStatus. Thêm transactionType/failureCode/failureMessage/occurredAt khi đối soát. payloadHash nên có độ dài đúng thuật toán, không TEXT. Không coi callback lặp là giao dịch thu tiền mới. |
| Refund → **Refund** | refunds | paymentId → paymentIntentId; currency → currencyCode. actorId → requestedByUserId nếu luôn là người yêu cầu. Thêm providerRefundId/completedAt/failureCode/failureMessage; shopOrderId khi hoàn theo shop. returnRequestId có thể null cho hoàn tiền không có trả hàng. |
| Coupon → **Coupon** | coupons | discountPercent → discountPercentage; maxDiscountAmount/minOrderValue → maximumDiscountAmount/minimumOrderAmount; startDate/expiryDate → startsAt/endsAt; usageLimit/usedCount → totalUsageLimit/redeemedCount; maxUsesPerUser → perUserUsageLimit. Thêm currencyCode cho tiền giảm cố định. Bỏ limitPerUser khi null perUserUsageLimit đã là không giới hạn. Giữ enabled; không thêm status EXPIRED cạnh tranh với endsAt. |
| CouponCategory → **CouponCategory** | coupon_categories | Giữ couponId/categoryId. Không cần thêm trường nghiệp vụ hoặc bảng mapping khác. Audit tối thiểu; activeStatus chỉ khi có luật phiên bản áp dụng riêng. Thay mapping trong transaction khóa coupon. |
| CouponProduct → **CouponProduct** | coupon_products | Giữ couponId/productId. variantId chỉ thêm nếu voucher thực sự giới hạn SKU; không đổi productId thành variantId khi luật hiện tại áp dụng toàn sản phẩm. |
| CouponRedemption → **CouponRedemption** | coupon_redemptions | Giữ discountAmount/status/expiresAt. Thêm redeemedAt/releasedAt/releaseReason. Không bỏ userId/shopId chỉ vì join được Order: đây có thể là snapshot hạn mức/phạm vi. redeemedCount tại Coupon là cache, cần đối soát từ lượt sử dụng. |
| Shipment → **Shipment** | shipments | sellerOrderId → shopOrderId; trackingNo → trackingNumber; carrier → carrierCode. Thêm carrierShipmentId/serviceCode; phí thực tế/currencyCode nếu cần. shopId có thể giữ cho authorization nhưng phải khớp ShopOrder, không để client sửa độc lập. |
| TrackingEvent → **ShipmentTrackingEvent** | shipment_tracking_events | status → shipmentStatus; actorId → actorUserId nếu phù hợp. Thêm providerEventId/carrierCode/receivedAt. occurredAt từ carrier khác thời điểm backend nhận. Không cập nhật lịch sử như bảng trạng thái hiện tại. |
| ReturnRequest → **ReturnRequest** | return_requests | Thêm shopOrderId/returnNumber nếu request được tách theo shop; giữ reviewedByUserId/reviewedAt. Không lưu approvedAt/reviewedAt cùng nghĩa nếu chưa có luật phân biệt. Đề xuất mỗi request thuộc một shop order để xử lý trả hàng đa shop rõ ràng. |
| ReturnItem → **ReturnRequestItem** | return_request_items | quantity → requestedQuantity. Thêm approvedQuantity/receivedQuantity/inspectionResultCode khi có kiểm hàng. requestedRefundAmount là số khách yêu cầu, không phải số đã được hoàn. |
| Review → **ProductReview** | product_reviews | reviewId → id; customerName → reviewerNameSnapshot; comment → content; status → moderationStatus; deleted → deletedAt. Thêm moderatedByUserId/moderatedAt/moderationReason nếu kiểm duyệt. Bỏ profileId/activeStatus; giữ snapshot danh tính hiển thị. orderItemId là căn cứ lần mua. |
| OutboxEvent → **OutboxEvent** | outbox_events | attempts → publishAttemptCount; payload → payloadJson; lastError → lastErrorMessage. Thêm eventVersion/nextAttemptAt; lockedUntil/lockedBy chỉ khi relay có claim. Giữ id số và eventId vì một cái là PK bản ghi, một cái là ID sự kiện liên hệ thống. |
| ConsumerInbox → **InboxEvent** | inbox_events | Giữ eventId/consumerName cho hệ thống event-only. Thêm receivedAt/processingStartedAt/processingAttemptCount/lastErrorMessage khi cần retry; lease khi có worker claim. Không dùng activeStatus làm điều kiện xử lý hoặc coi một cờ SUCCESS là đủ chống trùng đồng thời. |
| IdempotencyRecord → **IdempotencyRecord** | idempotency_records | responseStatus → responseStatusCode; operation → operationCode. Thêm completedAt; namespace/phạm vi user/shop/operation phải rõ; lockedUntil chỉ khi có xử lý đang chạy. Giữ requestHash. responseBody không chứa mật khẩu/token/bí mật. |
| AuditLog → **AuditEvent** | audit_events | action/resource → actionCode/resourceType. Với actor đa loại, thay actorId bằng actorType + actorReference String ổn định; nếu chỉ user thì actorUserId Long là đủ. Thêm occurredAt khi khác lúc ghi. Không giữ cả actorUserId/actorReference làm hai nguồn độc lập; bỏ audit cập nhật/activeStatus. |
| NotificationTemplate → **NotificationTemplate** | notification_templates | code → templateCode; locale → languageTag; channel → channelCode. Thêm templateVersion khi cần tái dựng nội dung cũ. Giữ enabled, bỏ activeStatus chung. variablesSchemaJson chỉ khi có validate biến template. |
| Notification → **Notification** | notifications | channel → channelCode; eventId → sourceEventId; content → body. Thêm templateVersion/attemptCount/nextAttemptAt/failureCode/failureMessage; deliveredAt cho kênh có delivery receipt. readAt không phải trạng thái gửi email. Mỗi dòng nên là một người nhận/một kênh. |
| FlashSaleCampaign → **FlashSaleCampaign** | flash_sale_campaigns | Giữ startsAt/endsAt/paymentTtlSeconds. campaignCode chỉ nếu cần mã nghiệp vụ. status không thay thế phép kiểm tra cửa sổ thời gian; không cần thêm một campaign chung thứ hai chỉ để đổi tên. |
| FlashSaleItem → **FlashSaleOffer** | flash_sale_offers | campaignId → flashSaleCampaignId; quota/reserved/sold → quotaQuantity/reservedQuantity/soldQuantity; maxPerUser → perUserQuantityLimit. Thêm currencyCode. Quota chiến dịch khác tồn vật lý; shopId phải khớp SKU/shop. |
| FlashSalePurchase → **FlashSaleAllocation** | flash_sale_allocations | campaignId → flashSaleCampaignId; flashSaleItemId → flashSaleOfferId; quantity → allocatedQuantity. Thêm confirmedAt/releasedAt/reservationId khi gắn giữ tồn. Bản ghi HELD chưa phải giao dịch mua, tên Allocation rõ nghĩa hơn Purchase. |
| SearchDocument → **ProductSearchDocument** | product_search_documents | content → searchableText; status → indexingStatus. Thêm indexName/indexVersion hoặc externalDocumentId theo adapter; lastErrorMessage/sourceVersion khi cần. Dữ liệu có thể xây lại, không dùng làm nguồn chốt giá/tồn. |
| ProductEmbedding → **ProductEmbedding** | product_embeddings | model → modelName; vectorReference → vectorId. Thêm providerCode/modelVersion/vectorStoreCode/dimensions/embeddedAt khi tích hợp vector store. sourceHash/sourceVersion xác định nội dung nguồn; không tự lưu thêm bản vector nếu chỉ quản lý reference. |
| AiConversation → **ShoppingConversation** | shopping_conversations | Giữ title/status/userId. Thêm lastMessageAt; closedAt nếu có đóng phiên. Không gắn model AI cố định vào conversation vì mỗi tin có thể dùng provider/model khác nhau. |
| AiMessage → **ConversationMessage** | conversation_messages | role → messageRole; model → modelName; status → processingStatus; inputTokens/outputTokens → inputTokenCount/outputTokenCount. Thêm providerCode/sequenceNumber khi cần; parentMessageId chỉ cho hội thoại phân nhánh. retrievalJson là snapshot nguồn, không phải nguồn giá/tồn. |
| SupportTicket → **SupportTicket** | support_tickets | assignedUserId → assignedToUserId; priority → priorityCode. Thêm ticketNumber/closedAt; categoryCode khi có phân loại hỗ trợ. orderId có thể null cho hỗ trợ chung; không tạo Dispute cho mọi ticket. |
| Dispute → **OrderDispute** | order_disputes | sellerOrderId → shopOrderId; assignedUserId → assignedToUserId; resolution → resolutionSummary. Thêm disputeNumber/resolvedAt/resolutionCode. Kết luận tranh chấp không tự thay thế thao tác Refund/Payment. |

## 6. Bộ trường nghiệp vụ đích cho các bảng lõi

Các danh sách sau cộng với audit phù hợp, không phải toàn bộ schema bắt buộc tại MVP:

- **UserAccount:** id, email, passwordHash, accountStatus, lockReason, lockedAt, emailVerifiedAt, lastLoginAt.
- **UserProfile:** id, userId, displayName, phoneNumber, genderCode, birthDate; avatarObjectKey khi cần.
- **Product:** id, shopId, categoryId, name, shortDescription, description, slug, status, publishedAt, soldQuantity, reviewCount, ratingSum hoặc averageRating cache, version. Giá tại Product chỉ còn là projection chỉ đọc hoặc giữ trong giai đoạn legacy.
- **ProductVariant:** id, productId, sku, attributesJson, price, salePrice, currencyCode, status, version; barcode và trường vận chuyển khi có nghiệp vụ.
- **StockBalance:** id, variantId, warehouseId khi đa kho, onHandQuantity, reservedQuantity, version. availableQuantity là kết quả tính.
- **Order:** id, userId, orderNumber, status, currencyCode, subtotalAmount, discountAmount, shippingAmount, totalAmount, idempotencyKey, placedAt, version.
- **ShopOrder:** id, orderId, shopId, shopOrderNumber, status, currencyCode, subtotalAmount, discountAmount, shippingAmount, totalAmount, version.
- **OrderItem:** id, orderId, shopOrderId, productId, variantId, productNameSnapshot, skuSnapshot, variantAttributesSnapshot, quantity, unitPrice, discountAmount, totalAmount, currencyCode.
- **PaymentIntent:** id, orderId, methodCode, status, amount, currencyCode, expiresAt, paidAt, version. Kết quả provider đặt theo giao dịch/adapter phù hợp.

Khi chưa có thuế/phí khác: subtotalAmount − discountAmount + shippingAmount = totalAmount. Nếu bổ sung thuế/phí, đặt tên thành phần rõ ràng, không nhét vào một trường totalPrice không rõ phạm vi. Không xóa các snapshot lịch sử chỉ vì join được dữ liệu hiện tại.

## 7. Có cần thêm entity mới không

Chưa cần thêm đại trà. Chỉ cân nhắc khi triển khai đúng nghiệp vụ:

| Entity | Điều kiện cần | Trường chính gợi ý |
|---|---|---|
| PaymentWebhookEvent | Ghi nhận từng callback, kể cả chưa tìm thấy payment hoặc bị từ chối; replay có kiểm soát | id, providerCode, providerEventId, paymentIntentId tùy chọn, receivedAt, processingStatus, payloadHash, sanitizedPayloadJson |
| RefundItem | Hoàn một phần theo dòng hàng/đơn đa shop | id, refundId, orderItemId, quantity, amount, currencyCode |
| SellerSettlement / SettlementItem | Nền tảng thu hộ rồi đối soát/chi trả seller | settlement theo shop/kỳ; item theo shopOrder, grossAmount, feeAmount, refundAmount, netAmount |
| ShipmentItem | Một ShopOrder chia nhiều kiện và cần biết kiện chứa hàng nào | id, shipmentId, orderItemId, quantity |
| ProductBrand | Có danh mục thương hiệu được quản trị/bộ lọc thương hiệu | id, name, slug |
| ConversationProductReference | Cần query/thống kê từng sản phẩm được dẫn trong message | id, conversationMessageId, productId, variantId, rank, sourceVersion |

Các bảng này cũng chỉ lưu ID thường. Không tự thêm ví tiền, ledger tài chính đầy đủ, KYC hoặc nhiều bảng thuộc tính khi phạm vi sản phẩm chưa cần.

## 8. Kiểu dữ liệu và index

- Tiền: BigDecimal và currencyCode thống nhất. Tỷ lệ phần trăm dùng precision phù hợp, không dùng precision tiền chỉ vì cùng là BigDecimal.
- Điện thoại, SKU, mã địa giới, mã vận đơn: String để giữ số 0 đầu/ký tự. ID nội bộ: Long. ID liên hệ thống/event/token family có thể là String.
- Thời điểm tuyệt đối: Instant. LocalDate dành cho ngày sinh/business date được định nghĩa rõ. Format dd/MM/yyyy đặt ở DTO/giao diện; không rải timezone/format hiển thị vào entity.
- Counter cần mặc định tại factory/service hoặc @Builder.Default có chủ đích. Không mặc định mọi số tiền bằng 0 để che dữ liệu giá chưa được nhập.
- Nullable vẫn được phép ở DB theo yêu cầu, nhưng service phải xác định trường bắt buộc theo từng trạng thái nghiệp vụ.
- Hash có độ dài theo thuật toán; JSON/TEXT cho nội dung dài. Không đưa hash vào TEXT chỉ vì tên có chữ payload.

Index **không unique** gợi ý theo truy vấn: Order(userId, createdAt); ShopOrder(shopId, status, createdAt); Product(shopId, status, categoryId); StockBalance(variantId, warehouseId); StockReservation(status, expiresAt); OutboxEvent(publishedAt, nextAttemptAt); InboxEvent(eventId, consumerName); IdempotencyRecord(userId, operationCode, idempotencyKey); PaymentTransaction(providerCode, providerEventId). Cần đối chiếu truy vấn và EXPLAIN trước khi thêm. Index thường không tự chống trùng.

## 9. Điểm có thể gây lỗi trong code hiện tại

| Vị trí | Bằng chứng | Hướng xử lý |
|---|---|---|
| ProductService.addProduct | Không gán soldCount/reviewCount/avgRating trong luồng tạo thông thường, field không có initializer | Factory/service hoặc mapper phải khởi tạo rõ ràng |
| OrderService.calculateSoldCount | getSoldCount() + quantity | Tránh unbox Integer null; xử lý dữ liệu legacy thiếu counter |
| ReviewService.calculateRatingStatsOrReviewCount | getReviewCount() + 1; getAvgRating() * getReviewCount() | Khởi tạo/cập nhật thống kê nhất quán; cân nhắc ratingSum/count |
| Coupon.isUsable / CouponService.deleteCoupon | usedCount < usageLimit hoặc getUsedCount() > 0 có thể unbox null | Null-safe theo quy tắc hoặc chuẩn hóa dữ liệu/factory |
| Audit mới | Chưa thấy cơ chế chung ghi createDate/updateDate/createBy/updateBy ngoài khai báo entity | Có auditing/lifecycle có actor trước khi dựa vào các cột này |

Rủi ro null phụ thuộc mapper, luồng tạo và bản ghi cụ thể; không khẳng định mọi request đều lỗi. Bản phân tích này không tự sửa các phần trên.

## 10. Lộ trình thực hiện

1. **Ổn định dữ liệu mới:** khởi tạo/null handling cho counter, tổng giỏ, currency và trạng thái. Không đổi 55 tên bảng trong một lần.
2. **Gom audit/trạng thái:** xác minh nguồn ghi; giữ một bộ thời gian/actor; tách lifecycle và soft-delete.
3. **Đổi tên Java trước:** dùng @Column giữ cột cũ khi cần; DTO có thể tiếp tục dùng tên API cũ để không buộc frontend đổi ngay.
4. **Chuyển dữ liệu nghiệp vụ:** userId, roleId, default SKU, nguồn giá, on-hand, PaymentIntent. Mỗi thay đổi một đợt có đối soát.
5. **Đổi vật lý:** cập nhật derived repository methods, JPQL, Criteria, MyBatis XML, MapStruct, template hóa đơn, seed/script, OpenAPI và Redis/cache chứa shape cũ.
6. **Bỏ trường cũ sau cùng:** kiểm tra ID mâu thuẫn, null, bản ghi lặp, tổng tiền, tồn kho và khả năng đọc dữ liệu lịch sử. Không drop/recreate DB chỉ để rename.

Các script/schema MySQL cũ đang được đánh dấu xóa trong workspace. Tài liệu này không khôi phục hoặc thay đổi chúng. Chưa chạy build/test vì chỉ thêm tài liệu và không đổi mã nguồn.
