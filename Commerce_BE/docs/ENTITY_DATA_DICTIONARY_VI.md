# Mô tả entity theo chuẩn tên mới

Đối chiếu 55 entity hiện tại. Tham chiếu lưu ID scalar, không FK hoặc collection quan hệ JPA. Schema chỉ có PK và index thường. Trạng thái là String; thời điểm là Instant UTC, ngày thuần là LocalDate. Audit được kế thừa và tự điền qua lifecycle JPA.

Xem [hướng dẫn chuyển dữ liệu](MODEL_NAMING_MIGRATION.md) và [manifest trước/sau](MODEL_NAMING_MANIFEST.json). Các entity là nền dữ liệu, không xác nhận mọi tính năng đã có API.

## UserAccount — users

Lưu tài khoản đăng nhập và trạng thái truy cập của một người dùng.

Tên cũ: `User` / `user`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `passwordHash` | `String` | `password` |
| `email` | `String` | `email` |
| `accountStatus` | `String` | `status` |
| `lockReason` | `String` | `lockedReason` |
| `lockedAt` | `Instant` | `lockedAt` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## UserProfile — user_profiles

Lưu thông tin cá nhân phục vụ mua hàng, tách khỏi dữ liệu đăng nhập.

Tên cũ: `UserProfile` / `user_profile`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `profileId` |
| `userId` | `Long` | `userId` |
| `displayName` | `String` | `username` |
| `gender` | `Boolean` | `gender` |
| `birthDate` | `LocalDate` | `birthDate` |
| `phoneNumber` | `String` | `mobileNumber` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## Role — roles

Lưu danh mục vai trò trong hệ thống.

Tên cũ: `Role` / `role`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | Mới |
| `code` | `String` | `roleName` |
| `name` | `String` | Mới |
| `description` | `String` | `description` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## UserRole — user_roles

Lưu việc gán vai trò cho tài khoản bằng một bảng mapping độc lập.

Tên cũ: `UserRole` / `user_role`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `userId` | `Long` | `userId` |
| `roleId` | `Long` | Mới |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## Permission — permissions

Lưu định nghĩa quyền theo tài nguyên, hành động và phạm vi.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `code` | `String` | `code` |
| `resourceCode` | `String` | `resource` |
| `actionCode` | `String` | `action` |
| `scopeCode` | `String` | `scope` |
| `description` | `String` | `description` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## RolePermission — role_permissions

Mapping giữa vai trò và quyền.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `roleId` | `Long` | Mới |
| `permissionId` | `Long` | `permissionId` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## RefreshSession — refresh_sessions

Lưu dữ liệu phiên refresh token cho cơ chế rotation và thu hồi theo nhóm phiên.

Tên cũ: `RefreshToken` / `refresh_tokens`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `userId` | `Long` | `userId` |
| `tokenHash` | `String` | `tokenHash` |
| `tokenFamilyId` | `String` | `familyId` |
| `expiresAt` | `Instant` | `expiresAt` |
| `revokedAt` | `Instant` | `revokedAt` |
| `replacedBySessionId` | `Long` | `replacedByTokenId` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## TokenRevocation — token_revocations

Lưu các token đã bị thu hồi để phục vụ blacklist hiện có.

Tên cũ: `RevokedToken` / `revoked_tokens`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `token` | `String` | `token` |
| `revokedAt` | `Instant` | `revokedAt` |
| `expiresAt` | `Instant` | `expiresAt` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## PasswordResetChallenge — password_reset_challenges

Lưu mã OTP dùng để đặt lại mật khẩu.

Tên cũ: `PasswordResetOtp` / `password_reset_otp`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `userId` | `Long` | `userId` |
| `otp` | `String` | `otp` |
| `expiresAt` | `Instant` | `expiredAt` |
| `used` | `boolean` | `used` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## UserAddress — user_addresses

Lưu sổ địa chỉ của khách hàng để chọn khi giao hàng.

Tên cũ: `Address` / `addresses`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `addressId` |
| `userProfileId` | `Long` | `profileId` |
| `recipientName` | `String` | `fullName` |
| `recipientPhone` | `String` | `phone` |
| `street` | `String` | `street` |
| `wardCode` | `Integer` | `wardCode` |
| `ward` | `String` | `ward` |
| `districtCode` | `Integer` | `districtID` |
| `district` | `String` | `district` |
| `provinceCode` | `Integer` | `provinceID` |
| `province` | `String` | `province` |
| `postalCode` | `String` | `postalCode` |
| `deliveryInstructions` | `String` | `additionalInfo` |
| `defaultShippingAddress` | `Boolean` | `isDefault` |
| `addressType` | `String` | `type` |
| `label` | `String` | `label` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## Shop — shops

Lưu gian hàng trong marketplace nhiều nhà bán.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `ownerUserId` | `Long` | `ownerUserId` |
| `name` | `String` | `name` |
| `slug` | `String` | `slug` |
| `description` | `String` | `description` |
| `status` | `String` | `status` |
| `approvedByUserId` | `Long` | `approvedByUserId` |
| `approvedAt` | `Instant` | `approvedAt` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## ShopMembership — shop_memberships

Lưu việc tài khoản tham gia và làm việc tại shop.

Tên cũ: `ShopMember` / `shop_members`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `shopId` | `Long` | `shopId` |
| `userId` | `Long` | `userId` |
| `memberRoleCode` | `String` | `role` |
| `status` | `String` | `status` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## Warehouse — warehouses

Lưu thông tin kho hàng thuộc shop.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `shopId` | `Long` | `shopId` |
| `name` | `String` | `name` |
| `addressId` | `Long` | `addressId` |
| `status` | `String` | `status` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## Category — categories

Lưu cây danh mục dùng để phân loại sản phẩm.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `categoryId` |
| `parentId` | `Long` | `parentId` |
| `name` | `String` | `categoryName` |
| `slug` | `String` | `slug` |
| `description` | `String` | `description` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## Product — products

Lưu thông tin chung của sản phẩm.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `productId` |
| `shopId` | `Long` | `shopId` |
| `categoryId` | `Long` | `categoryId` |
| `name` | `String` | `productName` |
| `description` | `String` | `description` |
| `price` | `BigDecimal` | `price` |
| `specialPrice` | `BigDecimal` | `specialPrice` |
| `discount` | `BigDecimal` | `discount` |
| `shortDescription` | `String` | `bio` |
| `slug` | `String` | `slug` |
| `height` | `Double` | `height` |
| `length` | `Double` | `length` |
| `weight` | `Double` | `weight` |
| `width` | `Double` | `width` |
| `origin` | `String` | `origin` |
| `soldQuantity` | `Integer` | `soldCount` |
| `reviewCount` | `Integer` | `reviewCount` |
| `averageRating` | `Double` | `avgRating` |
| `status` | `String` | `status` |
| `version` | `Long` | `version` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## ProductVariant — product_variants

Lưu biến thể bán được của sản phẩm, tương ứng SKU.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `productId` | `Long` | `productId` |
| `sku` | `String` | `sku` |
| `attributesJson` | `String` | `attributesJson` |
| `price` | `BigDecimal` | `price` |
| `salePrice` | `BigDecimal` | `specialPrice` |
| `currencyCode` | `String` | `currency` |
| `status` | `String` | `status` |
| `version` | `Long` | `version` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## ProductImage — product_images

Lưu ảnh gắn với sản phẩm hoặc biến thể.

Tên cũ: `Image` / `image`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `productId` | `Long` | `productId` |
| `variantId` | `Long` | `variantId` |
| `fileName` | `String` | `fileName` |
| `contentType` | `String` | `fileType` |
| `image` | `Blob` | `image` |
| `downloadUrl` | `String` | `downloadUrl` |
| `url` | `String` | `url` |
| `objectKey` | `String` | `objectKey` |
| `sortOrder` | `Integer` | `sortOrder` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## ShoppingCart — shopping_carts

Lưu thông tin giỏ hàng và tổng tiền.

Tên cũ: `Cart` / `carts`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `cartId` |
| `userProfileId` | `Long` | `profileId` |
| `userId` | `Long` | `userId` |
| `subtotalAmount` | `BigDecimal` | `totalPrice` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## CartItem — cart_items

Lưu một dòng sản phẩm hoặc SKU trong giỏ.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `cartItemId` |
| `cartId` | `Long` | `cartId` |
| `productId` | `Long` | `productId` |
| `variantId` | `Long` | `variantId` |
| `quantity` | `Integer` | `quantity` |
| `unitPrice` | `BigDecimal` | `unitPrice` |
| `lineAmount` | `BigDecimal` | `totalPrice` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## CheckoutQuote — checkout_quotes

Lưu bản báo giá có thời hạn trước khi tạo đơn.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `userId` | `Long` | `userId` |
| `addressId` | `Long` | `addressId` |
| `currencyCode` | `String` | `currency` |
| `subtotalAmount` | `BigDecimal` | `subtotal` |
| `discountAmount` | `BigDecimal` | `discountTotal` |
| `shippingAmount` | `BigDecimal` | `shippingTotal` |
| `totalAmount` | `BigDecimal` | `grandTotal` |
| `snapshotJson` | `String` | `snapshotJson` |
| `expiresAt` | `Instant` | `expiresAt` |
| `status` | `String` | `status` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## StockBalance — stock_balances

Lưu trạng thái tồn kho hiện tại của sản phẩm/SKU.

Tên cũ: `Inventory` / `inventory`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `inventoryId` |
| `productId` | `Long` | `productId` |
| `variantId` | `Long` | `variantId` |
| `onHandQuantity` | `int` | `available` |
| `reservedQuantity` | `int` | `reserved` |
| `version` | `Long` | `version` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## StockReservation — stock_reservations

Lưu một lần giữ hàng có thời hạn cho đơn.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `orderId` | `Long` | `orderId` |
| `variantId` | `Long` | `variantId` |
| `productId` | `Long` | `productId` |
| `reservedQuantity` | `Integer` | `quantity` |
| `status` | `String` | `status` |
| `expiresAt` | `Instant` | `expiresAt` |
| `version` | `Long` | `version` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## StockMovement — stock_movements

Lưu nhật ký biến động kho.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `variantId` | `Long` | `variantId` |
| `productId` | `Long` | `productId` |
| `quantityDelta` | `Integer` | `quantityDelta` |
| `reason` | `String` | `reason` |
| `referenceType` | `String` | `refType` |
| `referenceId` | `Long` | `refId` |
| `actorUserId` | `Long` | `actorId` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |

## Order — orders

Lưu đơn hàng cha của khách, tổng tiền và trạng thái tổng thể.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `orderId` |
| `userId` | `Long` | `userId` |
| `userProfileId` | `Long` | `profileId` |
| `paymentId` | `Long` | `paymentId` |
| `couponId` | `Long` | `couponId` |
| `orderNumber` | `String` | `orderNo` |
| `idempotencyKey` | `String` | `idempotencyKey` |
| `requestHash` | `String` | `requestHash` |
| `currencyCode` | `String` | `currency` |
| `orderDate` | `LocalDate` | `orderDate` |
| `subtotalAmount` | `BigDecimal` | `subtotal` |
| `shippingAmount` | `BigDecimal` | `shippingFee` |
| `discountAmount` | `BigDecimal` | `discountAmount` |
| `totalAmount` | `BigDecimal` | `totalAmount` |
| `status` | `String` | `orderStatus` |
| `version` | `Long` | `version` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## ShopOrder — shop_orders

Lưu phần đơn hàng thuộc một shop.

Tên cũ: `SellerOrder` / `seller_orders`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `orderId` | `Long` | `orderId` |
| `shopId` | `Long` | `shopId` |
| `status` | `String` | `status` |
| `subtotalAmount` | `BigDecimal` | `subtotal` |
| `shippingAmount` | `BigDecimal` | `shippingFee` |
| `discountAmount` | `BigDecimal` | `discountTotal` |
| `totalAmount` | `BigDecimal` | `grandTotal` |
| `version` | `Long` | `version` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## OrderItem — order_items

Lưu từng dòng hàng được mua và dữ liệu snapshot tại lúc đặt.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `orderItemId` |
| `orderId` | `Long` | `orderId` |
| `shopOrderId` | `Long` | `sellerOrderId` |
| `productId` | `Long` | `productId` |
| `variantId` | `Long` | `variantId` |
| `cartItemId` | `Long` | `cartItemId` |
| `productNameSnapshot` | `String` | `productTitle` |
| `skuSnapshot` | `String` | `sku` |
| `variantAttributesSnapshot` | `String` | `attributesSnapshot` |
| `quantity` | `Integer` | `quantity` |
| `unitPrice` | `BigDecimal` | `price` |
| `discountAmount` | `BigDecimal` | `discountAmount` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## OrderShippingAddress — order_shipping_addresses

Lưu bản sao địa chỉ nhận hàng gắn với đơn.

Tên cũ: `OrderDeliveryAddress` / `order_delivery_addresses`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `orderId` | `Long` | `orderId` |
| `recipientName` | `String` | `recipientName` |
| `recipientPhone` | `String` | `recipientPhone` |
| `street` | `String` | `street` |
| `ward` | `String` | `ward` |
| `district` | `String` | `district` |
| `province` | `String` | `province` |
| `postalCode` | `String` | `postalCode` |
| `deliveryInstructions` | `String` | `deliveryNote` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## OrderStatusTransition — order_status_transitions

Lưu lịch sử chuyển trạng thái của đơn.

Tên cũ: `OrderStatusHistory` / `order_status_history`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `historyId` |
| `orderId` | `Long` | `orderId` |
| `fromStatus` | `String` | `fromStatus` |
| `toStatus` | `String` | `orderStatus` |
| `actorUserId` | `Long` | `changedByUserId` |
| `reason` | `String` | `reason` |
| `occurredAt` | `Instant` | `changedAt` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |

## PaymentIntent — payment_intents

Lưu trạng thái thanh toán tổng thể của đơn và thông tin tích hợp cổng thanh toán hiện có.

Tên cũ: `Payment` / `payments`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `paymentId` |
| `orderId` | `Long` | `orderId` |
| `providerCode` | `String` | `provider` |
| `providerReference` | `String` | `providerRef` |
| `methodCode` | `String` | `paymentMethod` |
| `status` | `String` | `paymentStatus` |
| `vnpTxnRef` | `String` | `vnpTxnRef` |
| `amount` | `BigDecimal` | `amount` |
| `currencyCode` | `String` | `currency` |
| `orderInfo` | `String` | `orderInfo` |
| `bankCode` | `String` | `bankCode` |
| `responseCode` | `String` | `responseCode` |
| `providerStatus` | `String` | `status` |
| `paymentDate` | `Instant` | `paymentDate` |
| `expiresAt` | `Instant` | `expiredAt` |
| `providerTransactionId` | `String` | `transactionNo` |
| `cardType` | `String` | `cardType` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## PaymentTransaction — payment_transactions

Lưu từng sự kiện hoặc giao dịch từ nhà cung cấp thanh toán.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `paymentIntentId` | `Long` | `paymentId` |
| `providerCode` | `String` | `provider` |
| `providerEventId` | `String` | `providerEventId` |
| `providerTransactionId` | `String` | `providerRef` |
| `amount` | `BigDecimal` | `amount` |
| `currencyCode` | `String` | `currency` |
| `transactionStatus` | `String` | `status` |
| `payloadHash` | `String` | `payloadHash` |
| `receivedAt` | `Instant` | `receivedAt` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## Refund — refunds

Lưu yêu cầu và quá trình hoàn tiền.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `paymentIntentId` | `Long` | `paymentId` |
| `orderId` | `Long` | `orderId` |
| `returnRequestId` | `Long` | `returnRequestId` |
| `amount` | `BigDecimal` | `amount` |
| `currencyCode` | `String` | `currency` |
| `status` | `String` | `status` |
| `reason` | `String` | `reason` |
| `idempotencyKey` | `String` | `idempotencyKey` |
| `requestedByUserId` | `Long` | `actorId` |
| `version` | `Long` | `version` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## Coupon — coupons

Lưu mã giảm giá và điều kiện áp dụng.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `couponId` |
| `shopId` | `Long` | `shopId` |
| `code` | `String` | `code` |
| `scope` | `String` | `scope` |
| `discountType` | `String` | `discountType` |
| `discountPercentage` | `BigDecimal` | `discountPercent` |
| `discountAmount` | `BigDecimal` | `discountAmount` |
| `maximumDiscountAmount` | `BigDecimal` | `maxDiscountAmount` |
| `minimumOrderAmount` | `BigDecimal` | `minOrderValue` |
| `startsAt` | `Instant` | `startDate` |
| `endsAt` | `Instant` | `expiryDate` |
| `enabled` | `boolean` | `enabled` |
| `totalUsageLimit` | `Integer` | `usageLimit` |
| `redeemedCount` | `Integer` | `usedCount` |
| `limitPerUser` | `boolean` | `limitPerUser` |
| `perUserUsageLimit` | `Integer` | `maxUsesPerUser` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## CouponCategory — coupon_categories

Mapping giữa coupon và các danh mục được áp dụng.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `couponId` | `Long` | `couponId` |
| `categoryId` | `Long` | `categoryId` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## CouponProduct — coupon_products

Mapping giữa coupon và các sản phẩm được áp dụng.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `couponId` | `Long` | `couponId` |
| `productId` | `Long` | `productId` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## CouponRedemption — coupon_redemptions

Lưu việc khách giữ hoặc sử dụng coupon cho một đơn.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `couponId` | `Long` | `couponId` |
| `userId` | `Long` | `userId` |
| `orderId` | `Long` | `orderId` |
| `shopId` | `Long` | `shopId` |
| `status` | `String` | `status` |
| `discountAmount` | `BigDecimal` | `discountAmount` |
| `expiresAt` | `Instant` | `expiresAt` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## Shipment — shipments

Lưu thông tin vận đơn cho phần đơn của shop.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `shopOrderId` | `Long` | `sellerOrderId` |
| `shopId` | `Long` | `shopId` |
| `warehouseId` | `Long` | `warehouseId` |
| `assignedShipperId` | `Long` | `assignedShipperId` |
| `trackingNumber` | `String` | `trackingNo` |
| `carrierCode` | `String` | `carrier` |
| `status` | `String` | `status` |
| `shippedAt` | `Instant` | `shippedAt` |
| `deliveredAt` | `Instant` | `deliveredAt` |
| `version` | `Long` | `version` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## ShipmentTrackingEvent — shipment_tracking_events

Lưu từng mốc theo dõi của một vận đơn.

Tên cũ: `TrackingEvent` / `tracking_events`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `shipmentId` | `Long` | `shipmentId` |
| `shipmentStatus` | `String` | `status` |
| `description` | `String` | `description` |
| `location` | `String` | `location` |
| `actorUserId` | `Long` | `actorId` |
| `occurredAt` | `Instant` | `occurredAt` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |

## ReturnRequest — return_requests

Lưu yêu cầu trả hàng của khách.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `orderId` | `Long` | `orderId` |
| `userId` | `Long` | `userId` |
| `shopId` | `Long` | `shopId` |
| `status` | `String` | `status` |
| `reason` | `String` | `reason` |
| `reviewedByUserId` | `Long` | `reviewedByUserId` |
| `reviewedAt` | `Instant` | `reviewedAt` |
| `version` | `Long` | `version` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## ReturnRequestItem — return_request_items

Lưu những dòng hàng và số lượng cần trả trong một yêu cầu.

Tên cũ: `ReturnItem` / `return_items`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `returnRequestId` | `Long` | `returnRequestId` |
| `orderItemId` | `Long` | `orderItemId` |
| `requestedQuantity` | `Integer` | `quantity` |
| `reason` | `String` | `reason` |
| `requestedRefundAmount` | `BigDecimal` | `requestedRefundAmount` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## ProductReview — product_reviews

Lưu đánh giá sản phẩm sau khi mua.

Tên cũ: `Review` / `reviews`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `reviewId` |
| `productId` | `Long` | `productId` |
| `userProfileId` | `Long` | `profileId` |
| `userId` | `Long` | `userId` |
| `orderId` | `Long` | `orderId` |
| `orderItemId` | `Long` | `orderItemId` |
| `reviewerNameSnapshot` | `String` | `customerName` |
| `rating` | `int` | `rating` |
| `content` | `String` | `comment` |
| `moderationStatus` | `String` | `status` |
| `deleted` | `boolean` | `deleted` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## OutboxEvent — outbox_events

Lưu sự kiện cần phát sau khi transaction nghiệp vụ commit.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `eventId` | `String` | `eventId` |
| `aggregateType` | `String` | `aggregateType` |
| `aggregateId` | `String` | `aggregateId` |
| `eventType` | `String` | `eventType` |
| `aggregateVersion` | `Long` | `aggregateVersion` |
| `payloadJson` | `String` | `payload` |
| `correlationId` | `String` | `correlationId` |
| `occurredAt` | `Instant` | `occurredAt` |
| `publishedAt` | `Instant` | `publishedAt` |
| `publishAttemptCount` | `Integer` | `attempts` |
| `lastErrorMessage` | `String` | `lastError` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## InboxEvent — inbox_events

Ghi nhận sự kiện mà một consumer đã xử lý.

Tên cũ: `ConsumerInbox` / `consumer_inbox`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `eventId` | `String` | `eventId` |
| `consumerName` | `String` | `consumerName` |
| `processedAt` | `Instant` | `processedAt` |
| `status` | `String` | `status` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## IdempotencyRecord — idempotency_records

Lưu kết quả xử lý một command theo idempotency key.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `userId` | `Long` | `userId` |
| `operationCode` | `String` | `operation` |
| `idempotencyKey` | `String` | `idempotencyKey` |
| `requestHash` | `String` | `requestHash` |
| `status` | `String` | `status` |
| `responseStatusCode` | `Integer` | `responseStatus` |
| `responseBody` | `String` | `responseBody` |
| `expiresAt` | `Instant` | `expiresAt` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## AuditEvent — audit_events

Lưu lịch sử thao tác quan trọng của người dùng hoặc hệ thống.

Tên cũ: `AuditLog` / `audit_logs`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `actorUserId` | `Long` | `actorId` |
| `actionCode` | `String` | `action` |
| `resourceType` | `String` | `resource` |
| `resourceId` | `String` | `resourceId` |
| `beforeJson` | `String` | `beforeJson` |
| `afterJson` | `String` | `afterJson` |
| `correlationId` | `String` | `correlationId` |
| `clientIp` | `String` | `clientIp` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |

## NotificationTemplate — notification_templates

Lưu mẫu nội dung thông báo theo mã, ngôn ngữ và kênh.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `templateCode` | `String` | `code` |
| `languageTag` | `String` | `locale` |
| `channelCode` | `String` | `channel` |
| `subjectTemplate` | `String` | `subjectTemplate` |
| `bodyTemplate` | `String` | `bodyTemplate` |
| `enabled` | `Boolean` | `enabled` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## Notification — notifications

Lưu một thông báo cụ thể cần gửi hoặc đã gửi cho tài khoản.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `userId` | `Long` | `userId` |
| `channelCode` | `String` | `channel` |
| `templateCode` | `String` | `templateCode` |
| `subject` | `String` | `subject` |
| `body` | `String` | `content` |
| `status` | `String` | `status` |
| `sourceEventId` | `String` | `eventId` |
| `sentAt` | `Instant` | `sentAt` |
| `readAt` | `Instant` | `readAt` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## FlashSaleCampaign — flash_sale_campaigns

Lưu một chiến dịch flash sale.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `name` | `String` | `name` |
| `startsAt` | `Instant` | `startsAt` |
| `endsAt` | `Instant` | `endsAt` |
| `status` | `String` | `status` |
| `paymentTtlSeconds` | `Integer` | `paymentTtlSeconds` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## FlashSaleOffer — flash_sale_offers

Lưu SKU tham gia chiến dịch với giá và quota riêng.

Tên cũ: `FlashSaleItem` / `flash_sale_items`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `flashSaleCampaignId` | `Long` | `campaignId` |
| `variantId` | `Long` | `variantId` |
| `shopId` | `Long` | `shopId` |
| `salePrice` | `BigDecimal` | `salePrice` |
| `quotaQuantity` | `Integer` | `quota` |
| `reservedQuantity` | `Integer` | `reserved` |
| `soldQuantity` | `Integer` | `sold` |
| `perUserQuantityLimit` | `Integer` | `maxPerUser` |
| `version` | `Long` | `version` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## FlashSaleAllocation — flash_sale_allocations

Lưu lượt giữ suất hoặc mua flash sale của một khách.

Tên cũ: `FlashSalePurchase` / `flash_sale_purchases`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `flashSaleCampaignId` | `Long` | `campaignId` |
| `flashSaleOfferId` | `Long` | `flashSaleItemId` |
| `userId` | `Long` | `userId` |
| `orderId` | `Long` | `orderId` |
| `allocatedQuantity` | `Integer` | `quantity` |
| `status` | `String` | `status` |
| `expiresAt` | `Instant` | `expiresAt` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## ProductSearchDocument — product_search_documents

Lưu dữ liệu dẫn xuất phục vụ đưa sản phẩm/SKU vào chỉ mục tìm kiếm.

Tên cũ: `SearchDocument` / `search_documents`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `productId` | `Long` | `productId` |
| `variantId` | `Long` | `variantId` |
| `sourceHash` | `String` | `sourceHash` |
| `searchableText` | `String` | `content` |
| `indexingStatus` | `String` | `status` |
| `indexedAt` | `Instant` | `indexedAt` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## ProductEmbedding — product_embeddings

Lưu thông tin embedding của sản phẩm/SKU và nơi tham chiếu vector.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `productId` | `Long` | `productId` |
| `variantId` | `Long` | `variantId` |
| `modelName` | `String` | `model` |
| `vectorId` | `String` | `vectorReference` |
| `sourceHash` | `String` | `sourceHash` |
| `status` | `String` | `status` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## ShoppingConversation — shopping_conversations

Lưu phiên hội thoại giữa người dùng và trợ lý mua sắm.

Tên cũ: `AiConversation` / `ai_conversations`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `userId` | `Long` | `userId` |
| `title` | `String` | `title` |
| `status` | `String` | `status` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## ConversationMessage — conversation_messages

Lưu từng tin nhắn trong hội thoại AI.

Tên cũ: `AiMessage` / `ai_messages`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `conversationId` | `Long` | `conversationId` |
| `messageRole` | `String` | `role` |
| `content` | `String` | `content` |
| `retrievalJson` | `String` | `retrievalJson` |
| `modelName` | `String` | `model` |
| `inputTokenCount` | `Integer` | `inputTokens` |
| `outputTokenCount` | `Integer` | `outputTokens` |
| `processingStatus` | `String` | `status` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## SupportTicket — support_tickets

Lưu yêu cầu hỗ trợ của khách hàng.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `userId` | `Long` | `userId` |
| `orderId` | `Long` | `orderId` |
| `assignedToUserId` | `Long` | `assignedUserId` |
| `subject` | `String` | `subject` |
| `description` | `String` | `description` |
| `status` | `String` | `status` |
| `priorityCode` | `String` | `priority` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

## OrderDispute — order_disputes

Lưu tranh chấp cần giải quyết liên quan đơn hoặc yêu cầu trả hàng.

Tên cũ: `Dispute` / `disputes`.

| Trường | Kiểu Java | Tên trước khi đổi |
|---|---|---|
| `id` | `Long` | `id` |
| `orderId` | `Long` | `orderId` |
| `shopOrderId` | `Long` | `sellerOrderId` |
| `returnRequestId` | `Long` | `returnRequestId` |
| `openedByUserId` | `Long` | `openedByUserId` |
| `assignedToUserId` | `Long` | `assignedUserId` |
| `reason` | `String` | `reason` |
| `resolutionSummary` | `String` | `resolution` |
| `status` | `String` | `status` |
| `createdAt` | `Instant` | `createdAt` |
| `createdBy` | `String` | `createBy` |
| `updatedAt` | `Instant` | `updatedAt` |
| `updatedBy` | `String` | `updateBy` |

