-- MySQL 8.0.16+. Use an explicitly selected database.
-- No foreign keys, UNIQUE, CHECK, ENUM or non-PK NOT NULL constraints.
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `addresses` (
  `address_id` bigint NOT NULL AUTO_INCREMENT,
  `profile_id` bigint NULL,
  `full_name` varchar(255) NULL,
  `phone` varchar(255) NULL,
  `street` varchar(255) NULL,
  `ward_code` int NULL,
  `ward` varchar(255) NULL,
  `district_id` int NULL,
  `district` varchar(255) NULL,
  `province_id` int NULL,
  `province` varchar(255) NULL,
  `postal_code` varchar(255) NULL,
  `additional_info` text NULL,
  `is_default` bit(1) NULL,
  `type` varchar(255) NULL,
  `label` varchar(255) NULL,
  `created_at` datetime(6) NULL,
  `updated_at` datetime(6) NULL,
  PRIMARY KEY (`address_id`),
  KEY `idx_addresses_profile_id` (`profile_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `ai_conversations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL,
  `title` varchar(255) NULL,
  `status` varchar(255) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_ai_conversations_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `ai_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `conversation_id` bigint NULL,
  `role` varchar(255) NULL,
  `content` text NULL,
  `retrieval_json` text NULL,
  `model` varchar(255) NULL,
  `input_tokens` int NULL,
  `output_tokens` int NULL,
  `status` varchar(255) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_ai_messages_conversation_id` (`conversation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `audit_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `actor_id` bigint NULL,
  `action` varchar(255) NULL,
  `resource` varchar(255) NULL,
  `resource_id` varchar(255) NULL,
  `before_json` text NULL,
  `after_json` text NULL,
  `correlation_id` varchar(255) NULL,
  `client_ip` varchar(255) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_audit_logs_actor_id` (`actor_id`),
  KEY `idx_audit_logs_resource_id` (`resource_id`),
  KEY `idx_audit_logs_correlation_id` (`correlation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `cart_items` (
  `cart_item_id` bigint NOT NULL AUTO_INCREMENT,
  `cart_id` bigint NULL,
  `product_id` bigint NULL,
  `variant_id` bigint NULL,
  `quantity` int NULL,
  `unit_price` decimal(18,2) NULL,
  `total_price` decimal(18,2) NULL,
  PRIMARY KEY (`cart_item_id`),
  KEY `idx_cart_items_cart_id` (`cart_id`),
  KEY `idx_cart_items_product_id` (`product_id`),
  KEY `idx_cart_items_variant_id` (`variant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `carts` (
  `cart_id` bigint NOT NULL AUTO_INCREMENT,
  `profile_id` bigint NULL,
  `user_id` bigint NULL,
  `total_price` decimal(18,2) NULL,
  `updated_at` datetime(6) NULL,
  PRIMARY KEY (`cart_id`),
  KEY `idx_carts_profile_id` (`profile_id`),
  KEY `idx_carts_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `categories` (
  `category_id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NULL,
  `category_name` varchar(255) NULL,
  `slug` varchar(255) NULL,
  `description` text NULL,
  `create_at` date NULL,
  `update_at` date NULL,
  PRIMARY KEY (`category_id`),
  KEY `idx_categories_parent_id` (`parent_id`),
  KEY `idx_categories_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `checkout_quotes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL,
  `address_id` bigint NULL,
  `currency` varchar(255) NULL,
  `subtotal` decimal(18,2) NULL,
  `discount_total` decimal(18,2) NULL,
  `shipping_total` decimal(18,2) NULL,
  `grand_total` decimal(18,2) NULL,
  `snapshot_json` text NULL,
  `expires_at` timestamp(6) NULL,
  `status` varchar(255) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_checkout_quotes_user_id` (`user_id`),
  KEY `idx_checkout_quotes_address_id` (`address_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `consumer_inbox` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` varchar(255) NULL,
  `consumer_name` varchar(255) NULL,
  `processed_at` timestamp(6) NULL,
  `status` varchar(255) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_consumer_inbox_event_id` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `coupon_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `coupon_id` bigint NULL,
  `category_id` bigint NULL,
  PRIMARY KEY (`id`),
  KEY `idx_coupon_categories_coupon_id` (`coupon_id`),
  KEY `idx_coupon_categories_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `coupon_products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `coupon_id` bigint NULL,
  `product_id` bigint NULL,
  PRIMARY KEY (`id`),
  KEY `idx_coupon_products_coupon_id` (`coupon_id`),
  KEY `idx_coupon_products_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `coupon_redemptions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `coupon_id` bigint NULL,
  `user_id` bigint NULL,
  `order_id` bigint NULL,
  `shop_id` bigint NULL,
  `status` varchar(255) NULL,
  `discount_amount` decimal(18,2) NULL,
  `expires_at` timestamp(6) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_coupon_redemptions_coupon_id` (`coupon_id`),
  KEY `idx_coupon_redemptions_user_id` (`user_id`),
  KEY `idx_coupon_redemptions_order_id` (`order_id`),
  KEY `idx_coupon_redemptions_shop_id` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `coupons` (
  `coupon_id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint NULL,
  `code` varchar(255) NULL,
  `scope` varchar(255) NULL,
  `discount_type` varchar(255) NULL,
  `discount_percent` decimal(18,2) NULL,
  `discount_amount` decimal(18,2) NULL,
  `max_discount_amount` decimal(18,2) NULL,
  `min_order_value` decimal(18,2) NULL,
  `start_date` datetime(6) NULL,
  `expiry_date` datetime(6) NULL,
  `enabled` bit(1) NULL,
  `usage_limit` int NULL,
  `used_count` int NULL,
  `limit_per_user` bit(1) NULL,
  `max_uses_per_user` int NULL,
  PRIMARY KEY (`coupon_id`),
  KEY `idx_coupons_shop_id` (`shop_id`),
  KEY `idx_coupons_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `disputes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NULL,
  `seller_order_id` bigint NULL,
  `return_request_id` bigint NULL,
  `opened_by_user_id` bigint NULL,
  `assigned_user_id` bigint NULL,
  `reason` text NULL,
  `resolution` text NULL,
  `status` varchar(255) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_disputes_order_id` (`order_id`),
  KEY `idx_disputes_seller_order_id` (`seller_order_id`),
  KEY `idx_disputes_return_request_id` (`return_request_id`),
  KEY `idx_disputes_opened_by_user_id` (`opened_by_user_id`),
  KEY `idx_disputes_assigned_user_id` (`assigned_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `flash_sale_campaigns` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NULL,
  `starts_at` timestamp(6) NULL,
  `ends_at` timestamp(6) NULL,
  `status` varchar(255) NULL,
  `payment_ttl_seconds` int NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `flash_sale_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `campaign_id` bigint NULL,
  `variant_id` bigint NULL,
  `shop_id` bigint NULL,
  `sale_price` decimal(18,2) NULL,
  `quota` int NULL,
  `reserved` int NULL,
  `sold` int NULL,
  `max_per_user` int NULL,
  `version` bigint NULL DEFAULT 0,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_flash_sale_items_campaign_id` (`campaign_id`),
  KEY `idx_flash_sale_items_variant_id` (`variant_id`),
  KEY `idx_flash_sale_items_shop_id` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `flash_sale_purchases` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `campaign_id` bigint NULL,
  `flash_sale_item_id` bigint NULL,
  `user_id` bigint NULL,
  `order_id` bigint NULL,
  `quantity` int NULL,
  `status` varchar(255) NULL,
  `expires_at` timestamp(6) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_flash_sale_purchases_campaign_id` (`campaign_id`),
  KEY `idx_flash_sale_purchases_flash_sale_item_id` (`flash_sale_item_id`),
  KEY `idx_flash_sale_purchases_user_id` (`user_id`),
  KEY `idx_flash_sale_purchases_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `idempotency_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL,
  `operation` varchar(255) NULL,
  `idempotency_key` varchar(255) NULL,
  `request_hash` varchar(255) NULL,
  `status` varchar(255) NULL,
  `response_status` int NULL,
  `response_body` text NULL,
  `expires_at` timestamp(6) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_idempotency_records_user_id` (`user_id`),
  KEY `idx_idempotency_records_idempotency_key` (`idempotency_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `image` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NULL,
  `variant_id` bigint NULL,
  `file_name` varchar(255) NULL,
  `file_type` varchar(255) NULL,
  `image` longblob NULL,
  `download_url` varchar(255) NULL,
  `url` varchar(255) NULL,
  `object_key` varchar(255) NULL,
  `sort_order` int NULL,
  PRIMARY KEY (`id`),
  KEY `idx_image_product_id` (`product_id`),
  KEY `idx_image_variant_id` (`variant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `inventory` (
  `inventory_id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NULL,
  `variant_id` bigint NULL,
  `available` int NULL,
  `reserved` int NULL,
  `updated_at` datetime(6) NULL,
  `version` bigint NULL DEFAULT 0,
  PRIMARY KEY (`inventory_id`),
  KEY `idx_inventory_product_id` (`product_id`),
  KEY `idx_inventory_variant_id` (`variant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `notification_templates` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NULL,
  `locale` varchar(255) NULL,
  `channel` varchar(255) NULL,
  `subject_template` varchar(255) NULL,
  `body_template` text NULL,
  `enabled` bit(1) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_notification_templates_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL,
  `channel` varchar(255) NULL,
  `template_code` varchar(255) NULL,
  `subject` varchar(255) NULL,
  `content` text NULL,
  `status` varchar(255) NULL,
  `event_id` varchar(255) NULL,
  `sent_at` timestamp(6) NULL,
  `read_at` timestamp(6) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_notifications_user_id` (`user_id`),
  KEY `idx_notifications_event_id` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `order_delivery_addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NULL,
  `recipient_name` varchar(255) NULL,
  `recipient_phone` varchar(255) NULL,
  `street` varchar(255) NULL,
  `ward` varchar(255) NULL,
  `district` varchar(255) NULL,
  `province` varchar(255) NULL,
  `postal_code` varchar(255) NULL,
  `delivery_note` text NULL,
  `created_at` datetime(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_order_delivery_addresses_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `order_items` (
  `order_item_id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NULL,
  `seller_order_id` bigint NULL,
  `product_id` bigint NULL,
  `variant_id` bigint NULL,
  `cart_item_id` bigint NULL,
  `product_title` varchar(255) NULL,
  `sku` varchar(255) NULL,
  `attributes_snapshot` text NULL,
  `quantity` int NULL,
  `price` decimal(18,2) NULL,
  `discount_amount` decimal(18,2) NULL,
  PRIMARY KEY (`order_item_id`),
  KEY `idx_order_items_order_id` (`order_id`),
  KEY `idx_order_items_seller_order_id` (`seller_order_id`),
  KEY `idx_order_items_product_id` (`product_id`),
  KEY `idx_order_items_variant_id` (`variant_id`),
  KEY `idx_order_items_cart_item_id` (`cart_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `order_status_history` (
  `history_id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NULL,
  `from_status` varchar(255) NULL,
  `order_status` varchar(255) NULL,
  `changed_by_user_id` bigint NULL,
  `reason` varchar(255) NULL,
  `changed_at` datetime(6) NULL,
  PRIMARY KEY (`history_id`),
  KEY `idx_order_status_history_order_id` (`order_id`),
  KEY `idx_order_status_history_changed_by_user_id` (`changed_by_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `orders` (
  `order_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL,
  `profile_id` bigint NULL,
  `payment_id` bigint NULL,
  `coupon_id` bigint NULL,
  `order_no` varchar(255) NULL,
  `idempotency_key` varchar(255) NULL,
  `request_hash` varchar(255) NULL,
  `currency` varchar(255) NULL,
  `order_date` date NULL,
  `subtotal` decimal(18,2) NULL,
  `shipping_fee` decimal(18,2) NULL,
  `discount_amount` decimal(18,2) NULL,
  `total_amount` decimal(18,2) NULL,
  `order_status` varchar(255) NULL,
  `created_at` datetime(6) NULL,
  `updated_at` datetime(6) NULL,
  `version` bigint NULL DEFAULT 0,
  PRIMARY KEY (`order_id`),
  KEY `idx_orders_user_id` (`user_id`),
  KEY `idx_orders_profile_id` (`profile_id`),
  KEY `idx_orders_payment_id` (`payment_id`),
  KEY `idx_orders_coupon_id` (`coupon_id`),
  KEY `idx_orders_idempotency_key` (`idempotency_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `outbox_events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` varchar(255) NULL,
  `aggregate_type` varchar(255) NULL,
  `aggregate_id` varchar(255) NULL,
  `event_type` varchar(255) NULL,
  `aggregate_version` bigint NULL,
  `payload` text NULL,
  `correlation_id` varchar(255) NULL,
  `occurred_at` timestamp(6) NULL,
  `published_at` timestamp(6) NULL,
  `attempts` int NULL,
  `last_error` text NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_outbox_events_event_id` (`event_id`),
  KEY `idx_outbox_events_aggregate_id` (`aggregate_id`),
  KEY `idx_outbox_events_correlation_id` (`correlation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `password_reset_otp` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL,
  `otp` varchar(255) NULL,
  `created_at` datetime(6) NULL,
  `expired_at` datetime(6) NULL,
  `used` bit(1) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_password_reset_otp_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `payment_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payment_id` bigint NULL,
  `provider` varchar(255) NULL,
  `provider_event_id` varchar(255) NULL,
  `provider_ref` varchar(255) NULL,
  `amount` decimal(18,2) NULL,
  `currency` varchar(255) NULL,
  `status` varchar(255) NULL,
  `payload_hash` text NULL,
  `received_at` timestamp(6) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_payment_transactions_payment_id` (`payment_id`),
  KEY `idx_payment_transactions_provider_event_id` (`provider_event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `payments` (
  `payment_id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NULL,
  `provider` varchar(255) NULL,
  `provider_ref` varchar(255) NULL,
  `payment_method` varchar(255) NULL,
  `payment_status` varchar(255) NULL,
  `vnp_txn_ref` varchar(255) NULL,
  `amount` bigint NULL,
  `currency` varchar(255) NULL,
  `order_info` varchar(255) NULL,
  `bank_code` varchar(255) NULL,
  `response_code` varchar(255) NULL,
  `status` varchar(255) NULL,
  `payment_date` datetime(6) NULL,
  `expired_at` datetime(6) NULL,
  `transaction_no` varchar(255) NULL,
  `card_type` varchar(255) NULL,
  PRIMARY KEY (`payment_id`),
  KEY `idx_payments_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NULL,
  `resource` varchar(255) NULL,
  `action` varchar(255) NULL,
  `scope` varchar(255) NULL,
  `description` text NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_permissions_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `product_embeddings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NULL,
  `variant_id` bigint NULL,
  `model` varchar(255) NULL,
  `vector_reference` varchar(255) NULL,
  `source_hash` varchar(255) NULL,
  `status` varchar(255) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_product_embeddings_product_id` (`product_id`),
  KEY `idx_product_embeddings_variant_id` (`variant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `product_variants` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NULL,
  `sku` varchar(255) NULL,
  `attributes_json` text NULL,
  `price` decimal(18,2) NULL,
  `special_price` decimal(18,2) NULL,
  `currency` varchar(255) NULL,
  `status` varchar(255) NULL,
  `version` bigint NULL DEFAULT 0,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_product_variants_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `products` (
  `product_id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint NULL,
  `category_id` bigint NULL,
  `product_name` varchar(255) NULL,
  `description` text NULL,
  `price` decimal(18,2) NULL,
  `special_price` decimal(18,2) NULL,
  `discount` decimal(18,2) NULL,
  `bio` text NULL,
  `slug` varchar(255) NULL,
  `height` double NULL,
  `length` double NULL,
  `weight` double NULL,
  `width` double NULL,
  `origin` varchar(255) NULL,
  `sold_count` int NULL,
  `review_count` int NULL,
  `avg_rating` double NULL,
  `status` varchar(255) NULL,
  `create_at` date NULL,
  `update_at` date NULL,
  `version` bigint NULL DEFAULT 0,
  PRIMARY KEY (`product_id`),
  KEY `idx_products_shop_id` (`shop_id`),
  KEY `idx_products_category_id` (`category_id`),
  KEY `idx_products_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `refresh_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL,
  `token_hash` varchar(255) NULL,
  `family_id` varchar(255) NULL,
  `expires_at` timestamp(6) NULL,
  `revoked_at` timestamp(6) NULL,
  `replaced_by_token_id` bigint NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_refresh_tokens_user_id` (`user_id`),
  KEY `idx_refresh_tokens_family_id` (`family_id`),
  KEY `idx_refresh_tokens_replaced_by_token_id` (`replaced_by_token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `refunds` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payment_id` bigint NULL,
  `order_id` bigint NULL,
  `return_request_id` bigint NULL,
  `amount` decimal(18,2) NULL,
  `currency` varchar(255) NULL,
  `status` varchar(255) NULL,
  `reason` text NULL,
  `idempotency_key` varchar(255) NULL,
  `actor_id` bigint NULL,
  `version` bigint NULL DEFAULT 0,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_refunds_payment_id` (`payment_id`),
  KEY `idx_refunds_order_id` (`order_id`),
  KEY `idx_refunds_return_request_id` (`return_request_id`),
  KEY `idx_refunds_idempotency_key` (`idempotency_key`),
  KEY `idx_refunds_actor_id` (`actor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `return_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `return_request_id` bigint NULL,
  `order_item_id` bigint NULL,
  `quantity` int NULL,
  `reason` text NULL,
  `requested_refund_amount` decimal(18,2) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_return_items_return_request_id` (`return_request_id`),
  KEY `idx_return_items_order_item_id` (`order_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `return_requests` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NULL,
  `user_id` bigint NULL,
  `shop_id` bigint NULL,
  `status` varchar(255) NULL,
  `reason` text NULL,
  `reviewed_by_user_id` bigint NULL,
  `reviewed_at` timestamp(6) NULL,
  `version` bigint NULL DEFAULT 0,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_return_requests_order_id` (`order_id`),
  KEY `idx_return_requests_user_id` (`user_id`),
  KEY `idx_return_requests_shop_id` (`shop_id`),
  KEY `idx_return_requests_reviewed_by_user_id` (`reviewed_by_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `reviews` (
  `review_id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NULL,
  `profile_id` bigint NULL,
  `user_id` bigint NULL,
  `order_id` bigint NULL,
  `order_item_id` bigint NULL,
  `customer_name` varchar(255) NULL,
  `rating` int NULL,
  `comment` varchar(255) NULL,
  `status` varchar(255) NULL,
  `created_at` datetime(6) NULL,
  `deleted` bit(1) NULL,
  PRIMARY KEY (`review_id`),
  KEY `idx_reviews_product_id` (`product_id`),
  KEY `idx_reviews_profile_id` (`profile_id`),
  KEY `idx_reviews_user_id` (`user_id`),
  KEY `idx_reviews_order_id` (`order_id`),
  KEY `idx_reviews_order_item_id` (`order_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `revoked_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `token` text NULL,
  `revoked_at` timestamp(6) NULL,
  `expires_at` timestamp(6) NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `role` (
  `role_name` varchar(255) NOT NULL,
  `description` text NULL,
  PRIMARY KEY (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `role_permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) NULL,
  `permission_id` bigint NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_role_permissions_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `search_documents` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NULL,
  `variant_id` bigint NULL,
  `source_hash` varchar(255) NULL,
  `content` text NULL,
  `status` varchar(255) NULL,
  `indexed_at` timestamp(6) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_search_documents_product_id` (`product_id`),
  KEY `idx_search_documents_variant_id` (`variant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `seller_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NULL,
  `shop_id` bigint NULL,
  `status` varchar(255) NULL,
  `subtotal` decimal(18,2) NULL,
  `shipping_fee` decimal(18,2) NULL,
  `discount_total` decimal(18,2) NULL,
  `grand_total` decimal(18,2) NULL,
  `version` bigint NULL DEFAULT 0,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_seller_orders_order_id` (`order_id`),
  KEY `idx_seller_orders_shop_id` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `shipments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `seller_order_id` bigint NULL,
  `shop_id` bigint NULL,
  `warehouse_id` bigint NULL,
  `assigned_shipper_id` bigint NULL,
  `tracking_no` varchar(255) NULL,
  `carrier` varchar(255) NULL,
  `status` varchar(255) NULL,
  `shipped_at` timestamp(6) NULL,
  `delivered_at` timestamp(6) NULL,
  `version` bigint NULL DEFAULT 0,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_shipments_seller_order_id` (`seller_order_id`),
  KEY `idx_shipments_shop_id` (`shop_id`),
  KEY `idx_shipments_warehouse_id` (`warehouse_id`),
  KEY `idx_shipments_assigned_shipper_id` (`assigned_shipper_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `shop_members` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint NULL,
  `user_id` bigint NULL,
  `role` varchar(255) NULL,
  `status` varchar(255) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_shop_members_shop_id` (`shop_id`),
  KEY `idx_shop_members_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `shops` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `owner_user_id` bigint NULL,
  `name` varchar(255) NULL,
  `slug` varchar(255) NULL,
  `description` text NULL,
  `status` varchar(255) NULL,
  `approved_by_user_id` bigint NULL,
  `approved_at` timestamp(6) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_shops_owner_user_id` (`owner_user_id`),
  KEY `idx_shops_slug` (`slug`),
  KEY `idx_shops_approved_by_user_id` (`approved_by_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `stock_movements` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `variant_id` bigint NULL,
  `product_id` bigint NULL,
  `quantity_delta` int NULL,
  `reason` text NULL,
  `ref_type` varchar(255) NULL,
  `ref_id` bigint NULL,
  `actor_id` bigint NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_stock_movements_variant_id` (`variant_id`),
  KEY `idx_stock_movements_product_id` (`product_id`),
  KEY `idx_stock_movements_ref_id` (`ref_id`),
  KEY `idx_stock_movements_actor_id` (`actor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `stock_reservations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NULL,
  `variant_id` bigint NULL,
  `product_id` bigint NULL,
  `quantity` int NULL,
  `status` varchar(255) NULL,
  `expires_at` timestamp(6) NULL,
  `version` bigint NULL DEFAULT 0,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_stock_reservations_order_id` (`order_id`),
  KEY `idx_stock_reservations_variant_id` (`variant_id`),
  KEY `idx_stock_reservations_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `support_tickets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL,
  `order_id` bigint NULL,
  `assigned_user_id` bigint NULL,
  `subject` varchar(255) NULL,
  `description` text NULL,
  `status` varchar(255) NULL,
  `priority` varchar(255) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_support_tickets_user_id` (`user_id`),
  KEY `idx_support_tickets_order_id` (`order_id`),
  KEY `idx_support_tickets_assigned_user_id` (`assigned_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `tracking_events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shipment_id` bigint NULL,
  `status` varchar(255) NULL,
  `description` text NULL,
  `location` varchar(255) NULL,
  `actor_id` bigint NULL,
  `occurred_at` timestamp(6) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tracking_events_shipment_id` (`shipment_id`),
  KEY `idx_tracking_events_actor_id` (`actor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `password` varchar(255) NULL,
  `email` varchar(255) NULL,
  `enabled` bit(1) NULL,
  `status` varchar(255) NULL,
  `locked_reason` varchar(255) NULL,
  `locked_at` datetime(6) NULL,
  `created_at` datetime(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `user_profile` (
  `profile_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL,
  `username` varchar(255) NULL,
  `gender` bit(1) NULL,
  `birth_date` date NULL,
  `mobile_number` varchar(255) NULL,
  PRIMARY KEY (`profile_id`),
  KEY `idx_user_profile_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL,
  `role_name` varchar(255) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_role_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `warehouses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint NULL,
  `name` varchar(255) NULL,
  `address_id` bigint NULL,
  `status` varchar(255) NULL,
  `created_at` timestamp(6) NULL,
  `updated_at` timestamp(6) NULL,
  PRIMARY KEY (`id`),
  KEY `idx_warehouses_shop_id` (`shop_id`),
  KEY `idx_warehouses_address_id` (`address_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
