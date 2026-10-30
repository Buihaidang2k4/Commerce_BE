-- Run 01_create_scalar_schema.sql first. Stop application writers and take a verified backup.
-- MySQL DDL commits implicitly; this script is not an atomic rollbackable migration.
-- This changes schema/data mappings, never drops application tables or rows.
SET NAMES utf8mb4;

DELIMITER $$
DROP PROCEDURE IF EXISTS sc_drop_constraints$$
CREATE PROCEDURE sc_drop_constraints()
BEGIN
  DECLARE done INT DEFAULT 0;
  DECLARE t VARCHAR(255); DECLARE c VARCHAR(255); DECLARE kind VARCHAR(40);
  DECLARE cur CURSOR FOR SELECT TABLE_NAME, CONSTRAINT_NAME, CONSTRAINT_TYPE FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME IN ('addresses','ai_conversations','ai_messages','audit_logs','cart_items','carts','categories','checkout_quotes','consumer_inbox','coupon_categories','coupon_products','coupon_redemptions','coupons','disputes','flash_sale_campaigns','flash_sale_items','flash_sale_purchases','idempotency_records','image','inventory','notification_templates','notifications','order_delivery_addresses','order_items','order_status_history','orders','outbox_events','password_reset_otp','payment_transactions','payments','permissions','product_embeddings','product_variants','products','refresh_tokens','refunds','return_items','return_requests','reviews','revoked_tokens','role','role_permissions','search_documents','seller_orders','shipments','shop_members','shops','stock_movements','stock_reservations','support_tickets','tracking_events','user','user_profile','user_role','warehouses')
      AND CONSTRAINT_TYPE IN ('FOREIGN KEY','UNIQUE','CHECK')
    ORDER BY CASE CONSTRAINT_TYPE WHEN 'FOREIGN KEY' THEN 0 WHEN 'CHECK' THEN 1 ELSE 2 END;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
  OPEN cur;
  read_loop: LOOP
    FETCH cur INTO t,c,kind; IF done THEN LEAVE read_loop; END IF;
    SET @sc_sql = CONCAT('ALTER TABLE `',REPLACE(t,'`','``'),'` DROP ',CASE kind WHEN 'FOREIGN KEY' THEN 'FOREIGN KEY' WHEN 'CHECK' THEN 'CHECK' ELSE 'INDEX' END,' `',REPLACE(c,'`','``'),'`');
    PREPARE sc_stmt FROM @sc_sql; EXECUTE sc_stmt; DEALLOCATE PREPARE sc_stmt;
  END LOOP;
  CLOSE cur;
END$$
CALL sc_drop_constraints()$$
DROP PROCEDURE sc_drop_constraints$$

DROP PROCEDURE IF EXISTS sc_mapping_id$$
CREATE PROCEDURE sc_mapping_id(IN t VARCHAR(255))
BEGIN
  IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=t AND COLUMN_NAME='id') THEN
    IF EXISTS (SELECT 1 FROM information_schema.TABLE_CONSTRAINTS WHERE CONSTRAINT_SCHEMA=DATABASE() AND TABLE_NAME=t AND CONSTRAINT_TYPE='PRIMARY KEY') THEN
      SET @sc_sql=CONCAT('ALTER TABLE `',t,'` DROP PRIMARY KEY');
      PREPARE sc_stmt FROM @sc_sql; EXECUTE sc_stmt; DEALLOCATE PREPARE sc_stmt;
    END IF;
    SET @sc_sql=CONCAT('ALTER TABLE `',t,'` ADD COLUMN id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY');
    PREPARE sc_stmt FROM @sc_sql; EXECUTE sc_stmt; DEALLOCATE PREPARE sc_stmt;
  END IF;
END$$
CALL sc_mapping_id('user_role')$$
CALL sc_mapping_id('coupon_categories')$$
CALL sc_mapping_id('coupon_products')$$
DROP PROCEDURE sc_mapping_id$$

DROP PROCEDURE IF EXISTS sc_add_column$$
CREATE PROCEDURE sc_add_column(IN t VARCHAR(255), IN c VARCHAR(255), IN definition TEXT)
BEGIN
  IF NOT EXISTS (SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=t AND COLUMN_NAME=c) THEN
    SET @sc_sql=CONCAT('ALTER TABLE `',t,'` ADD COLUMN `',c,'` ',definition);
    PREPARE sc_stmt FROM @sc_sql; EXECUTE sc_stmt; DEALLOCATE PREPARE sc_stmt;
  END IF;
END$$
CALL sc_add_column('addresses','profile_id','bigint NULL')$$
CALL sc_add_column('addresses','full_name','varchar(255) NULL')$$
CALL sc_add_column('addresses','phone','varchar(255) NULL')$$
CALL sc_add_column('addresses','street','varchar(255) NULL')$$
CALL sc_add_column('addresses','ward_code','int NULL')$$
CALL sc_add_column('addresses','ward','varchar(255) NULL')$$
CALL sc_add_column('addresses','district_id','int NULL')$$
CALL sc_add_column('addresses','district','varchar(255) NULL')$$
CALL sc_add_column('addresses','province_id','int NULL')$$
CALL sc_add_column('addresses','province','varchar(255) NULL')$$
CALL sc_add_column('addresses','postal_code','varchar(255) NULL')$$
CALL sc_add_column('addresses','additional_info','text NULL')$$
CALL sc_add_column('addresses','is_default','bit(1) NULL')$$
CALL sc_add_column('addresses','type','varchar(255) NULL')$$
CALL sc_add_column('addresses','label','varchar(255) NULL')$$
CALL sc_add_column('addresses','created_at','datetime(6) NULL')$$
CALL sc_add_column('addresses','updated_at','datetime(6) NULL')$$
CALL sc_add_column('ai_conversations','user_id','bigint NULL')$$
CALL sc_add_column('ai_conversations','title','varchar(255) NULL')$$
CALL sc_add_column('ai_conversations','status','varchar(255) NULL')$$
CALL sc_add_column('ai_conversations','created_at','timestamp(6) NULL')$$
CALL sc_add_column('ai_conversations','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('ai_messages','conversation_id','bigint NULL')$$
CALL sc_add_column('ai_messages','role','varchar(255) NULL')$$
CALL sc_add_column('ai_messages','content','text NULL')$$
CALL sc_add_column('ai_messages','retrieval_json','text NULL')$$
CALL sc_add_column('ai_messages','model','varchar(255) NULL')$$
CALL sc_add_column('ai_messages','input_tokens','int NULL')$$
CALL sc_add_column('ai_messages','output_tokens','int NULL')$$
CALL sc_add_column('ai_messages','status','varchar(255) NULL')$$
CALL sc_add_column('ai_messages','created_at','timestamp(6) NULL')$$
CALL sc_add_column('ai_messages','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('audit_logs','actor_id','bigint NULL')$$
CALL sc_add_column('audit_logs','action','varchar(255) NULL')$$
CALL sc_add_column('audit_logs','resource','varchar(255) NULL')$$
CALL sc_add_column('audit_logs','resource_id','varchar(255) NULL')$$
CALL sc_add_column('audit_logs','before_json','text NULL')$$
CALL sc_add_column('audit_logs','after_json','text NULL')$$
CALL sc_add_column('audit_logs','correlation_id','varchar(255) NULL')$$
CALL sc_add_column('audit_logs','client_ip','varchar(255) NULL')$$
CALL sc_add_column('audit_logs','created_at','timestamp(6) NULL')$$
CALL sc_add_column('audit_logs','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('cart_items','cart_id','bigint NULL')$$
CALL sc_add_column('cart_items','product_id','bigint NULL')$$
CALL sc_add_column('cart_items','variant_id','bigint NULL')$$
CALL sc_add_column('cart_items','quantity','int NULL')$$
CALL sc_add_column('cart_items','unit_price','decimal(18,2) NULL')$$
CALL sc_add_column('cart_items','total_price','decimal(18,2) NULL')$$
CALL sc_add_column('carts','profile_id','bigint NULL')$$
CALL sc_add_column('carts','user_id','bigint NULL')$$
CALL sc_add_column('carts','total_price','decimal(18,2) NULL')$$
CALL sc_add_column('carts','updated_at','datetime(6) NULL')$$
CALL sc_add_column('categories','parent_id','bigint NULL')$$
CALL sc_add_column('categories','category_name','varchar(255) NULL')$$
CALL sc_add_column('categories','slug','varchar(255) NULL')$$
CALL sc_add_column('categories','description','text NULL')$$
CALL sc_add_column('categories','create_at','date NULL')$$
CALL sc_add_column('categories','update_at','date NULL')$$
CALL sc_add_column('checkout_quotes','user_id','bigint NULL')$$
CALL sc_add_column('checkout_quotes','address_id','bigint NULL')$$
CALL sc_add_column('checkout_quotes','currency','varchar(255) NULL')$$
CALL sc_add_column('checkout_quotes','subtotal','decimal(18,2) NULL')$$
CALL sc_add_column('checkout_quotes','discount_total','decimal(18,2) NULL')$$
CALL sc_add_column('checkout_quotes','shipping_total','decimal(18,2) NULL')$$
CALL sc_add_column('checkout_quotes','grand_total','decimal(18,2) NULL')$$
CALL sc_add_column('checkout_quotes','snapshot_json','text NULL')$$
CALL sc_add_column('checkout_quotes','expires_at','timestamp(6) NULL')$$
CALL sc_add_column('checkout_quotes','status','varchar(255) NULL')$$
CALL sc_add_column('checkout_quotes','created_at','timestamp(6) NULL')$$
CALL sc_add_column('checkout_quotes','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('consumer_inbox','event_id','varchar(255) NULL')$$
CALL sc_add_column('consumer_inbox','consumer_name','varchar(255) NULL')$$
CALL sc_add_column('consumer_inbox','processed_at','timestamp(6) NULL')$$
CALL sc_add_column('consumer_inbox','status','varchar(255) NULL')$$
CALL sc_add_column('consumer_inbox','created_at','timestamp(6) NULL')$$
CALL sc_add_column('consumer_inbox','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('coupon_categories','coupon_id','bigint NULL')$$
CALL sc_add_column('coupon_categories','category_id','bigint NULL')$$
CALL sc_add_column('coupon_products','coupon_id','bigint NULL')$$
CALL sc_add_column('coupon_products','product_id','bigint NULL')$$
CALL sc_add_column('coupon_redemptions','coupon_id','bigint NULL')$$
CALL sc_add_column('coupon_redemptions','user_id','bigint NULL')$$
CALL sc_add_column('coupon_redemptions','order_id','bigint NULL')$$
CALL sc_add_column('coupon_redemptions','shop_id','bigint NULL')$$
CALL sc_add_column('coupon_redemptions','status','varchar(255) NULL')$$
CALL sc_add_column('coupon_redemptions','discount_amount','decimal(18,2) NULL')$$
CALL sc_add_column('coupon_redemptions','expires_at','timestamp(6) NULL')$$
CALL sc_add_column('coupon_redemptions','created_at','timestamp(6) NULL')$$
CALL sc_add_column('coupon_redemptions','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('coupons','shop_id','bigint NULL')$$
CALL sc_add_column('coupons','code','varchar(255) NULL')$$
CALL sc_add_column('coupons','scope','varchar(255) NULL')$$
CALL sc_add_column('coupons','discount_type','varchar(255) NULL')$$
CALL sc_add_column('coupons','discount_percent','decimal(18,2) NULL')$$
CALL sc_add_column('coupons','discount_amount','decimal(18,2) NULL')$$
CALL sc_add_column('coupons','max_discount_amount','decimal(18,2) NULL')$$
CALL sc_add_column('coupons','min_order_value','decimal(18,2) NULL')$$
CALL sc_add_column('coupons','start_date','datetime(6) NULL')$$
CALL sc_add_column('coupons','expiry_date','datetime(6) NULL')$$
CALL sc_add_column('coupons','enabled','bit(1) NULL')$$
CALL sc_add_column('coupons','usage_limit','int NULL')$$
CALL sc_add_column('coupons','used_count','int NULL')$$
CALL sc_add_column('coupons','limit_per_user','bit(1) NULL')$$
CALL sc_add_column('coupons','max_uses_per_user','int NULL')$$
CALL sc_add_column('disputes','order_id','bigint NULL')$$
CALL sc_add_column('disputes','seller_order_id','bigint NULL')$$
CALL sc_add_column('disputes','return_request_id','bigint NULL')$$
CALL sc_add_column('disputes','opened_by_user_id','bigint NULL')$$
CALL sc_add_column('disputes','assigned_user_id','bigint NULL')$$
CALL sc_add_column('disputes','reason','text NULL')$$
CALL sc_add_column('disputes','resolution','text NULL')$$
CALL sc_add_column('disputes','status','varchar(255) NULL')$$
CALL sc_add_column('disputes','created_at','timestamp(6) NULL')$$
CALL sc_add_column('disputes','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('flash_sale_campaigns','name','varchar(255) NULL')$$
CALL sc_add_column('flash_sale_campaigns','starts_at','timestamp(6) NULL')$$
CALL sc_add_column('flash_sale_campaigns','ends_at','timestamp(6) NULL')$$
CALL sc_add_column('flash_sale_campaigns','status','varchar(255) NULL')$$
CALL sc_add_column('flash_sale_campaigns','payment_ttl_seconds','int NULL')$$
CALL sc_add_column('flash_sale_campaigns','created_at','timestamp(6) NULL')$$
CALL sc_add_column('flash_sale_campaigns','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('flash_sale_items','campaign_id','bigint NULL')$$
CALL sc_add_column('flash_sale_items','variant_id','bigint NULL')$$
CALL sc_add_column('flash_sale_items','shop_id','bigint NULL')$$
CALL sc_add_column('flash_sale_items','sale_price','decimal(18,2) NULL')$$
CALL sc_add_column('flash_sale_items','quota','int NULL')$$
CALL sc_add_column('flash_sale_items','reserved','int NULL')$$
CALL sc_add_column('flash_sale_items','sold','int NULL')$$
CALL sc_add_column('flash_sale_items','max_per_user','int NULL')$$
CALL sc_add_column('flash_sale_items','version','bigint NULL DEFAULT 0')$$
CALL sc_add_column('flash_sale_items','created_at','timestamp(6) NULL')$$
CALL sc_add_column('flash_sale_items','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('flash_sale_purchases','campaign_id','bigint NULL')$$
CALL sc_add_column('flash_sale_purchases','flash_sale_item_id','bigint NULL')$$
CALL sc_add_column('flash_sale_purchases','user_id','bigint NULL')$$
CALL sc_add_column('flash_sale_purchases','order_id','bigint NULL')$$
CALL sc_add_column('flash_sale_purchases','quantity','int NULL')$$
CALL sc_add_column('flash_sale_purchases','status','varchar(255) NULL')$$
CALL sc_add_column('flash_sale_purchases','expires_at','timestamp(6) NULL')$$
CALL sc_add_column('flash_sale_purchases','created_at','timestamp(6) NULL')$$
CALL sc_add_column('flash_sale_purchases','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('idempotency_records','user_id','bigint NULL')$$
CALL sc_add_column('idempotency_records','operation','varchar(255) NULL')$$
CALL sc_add_column('idempotency_records','idempotency_key','varchar(255) NULL')$$
CALL sc_add_column('idempotency_records','request_hash','varchar(255) NULL')$$
CALL sc_add_column('idempotency_records','status','varchar(255) NULL')$$
CALL sc_add_column('idempotency_records','response_status','int NULL')$$
CALL sc_add_column('idempotency_records','response_body','text NULL')$$
CALL sc_add_column('idempotency_records','expires_at','timestamp(6) NULL')$$
CALL sc_add_column('idempotency_records','created_at','timestamp(6) NULL')$$
CALL sc_add_column('idempotency_records','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('image','product_id','bigint NULL')$$
CALL sc_add_column('image','variant_id','bigint NULL')$$
CALL sc_add_column('image','file_name','varchar(255) NULL')$$
CALL sc_add_column('image','file_type','varchar(255) NULL')$$
CALL sc_add_column('image','image','longblob NULL')$$
CALL sc_add_column('image','download_url','varchar(255) NULL')$$
CALL sc_add_column('image','url','varchar(255) NULL')$$
CALL sc_add_column('image','object_key','varchar(255) NULL')$$
CALL sc_add_column('image','sort_order','int NULL')$$
CALL sc_add_column('inventory','product_id','bigint NULL')$$
CALL sc_add_column('inventory','variant_id','bigint NULL')$$
CALL sc_add_column('inventory','available','int NULL')$$
CALL sc_add_column('inventory','reserved','int NULL')$$
CALL sc_add_column('inventory','updated_at','datetime(6) NULL')$$
CALL sc_add_column('inventory','version','bigint NULL DEFAULT 0')$$
CALL sc_add_column('notification_templates','code','varchar(255) NULL')$$
CALL sc_add_column('notification_templates','locale','varchar(255) NULL')$$
CALL sc_add_column('notification_templates','channel','varchar(255) NULL')$$
CALL sc_add_column('notification_templates','subject_template','varchar(255) NULL')$$
CALL sc_add_column('notification_templates','body_template','text NULL')$$
CALL sc_add_column('notification_templates','enabled','bit(1) NULL')$$
CALL sc_add_column('notification_templates','created_at','timestamp(6) NULL')$$
CALL sc_add_column('notification_templates','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('notifications','user_id','bigint NULL')$$
CALL sc_add_column('notifications','channel','varchar(255) NULL')$$
CALL sc_add_column('notifications','template_code','varchar(255) NULL')$$
CALL sc_add_column('notifications','subject','varchar(255) NULL')$$
CALL sc_add_column('notifications','content','text NULL')$$
CALL sc_add_column('notifications','status','varchar(255) NULL')$$
CALL sc_add_column('notifications','event_id','varchar(255) NULL')$$
CALL sc_add_column('notifications','sent_at','timestamp(6) NULL')$$
CALL sc_add_column('notifications','read_at','timestamp(6) NULL')$$
CALL sc_add_column('notifications','created_at','timestamp(6) NULL')$$
CALL sc_add_column('notifications','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('order_delivery_addresses','order_id','bigint NULL')$$
CALL sc_add_column('order_delivery_addresses','recipient_name','varchar(255) NULL')$$
CALL sc_add_column('order_delivery_addresses','recipient_phone','varchar(255) NULL')$$
CALL sc_add_column('order_delivery_addresses','street','varchar(255) NULL')$$
CALL sc_add_column('order_delivery_addresses','ward','varchar(255) NULL')$$
CALL sc_add_column('order_delivery_addresses','district','varchar(255) NULL')$$
CALL sc_add_column('order_delivery_addresses','province','varchar(255) NULL')$$
CALL sc_add_column('order_delivery_addresses','postal_code','varchar(255) NULL')$$
CALL sc_add_column('order_delivery_addresses','delivery_note','text NULL')$$
CALL sc_add_column('order_delivery_addresses','created_at','datetime(6) NULL')$$
CALL sc_add_column('order_items','order_id','bigint NULL')$$
CALL sc_add_column('order_items','seller_order_id','bigint NULL')$$
CALL sc_add_column('order_items','product_id','bigint NULL')$$
CALL sc_add_column('order_items','variant_id','bigint NULL')$$
CALL sc_add_column('order_items','cart_item_id','bigint NULL')$$
CALL sc_add_column('order_items','product_title','varchar(255) NULL')$$
CALL sc_add_column('order_items','sku','varchar(255) NULL')$$
CALL sc_add_column('order_items','attributes_snapshot','text NULL')$$
CALL sc_add_column('order_items','quantity','int NULL')$$
CALL sc_add_column('order_items','price','decimal(18,2) NULL')$$
CALL sc_add_column('order_items','discount_amount','decimal(18,2) NULL')$$
CALL sc_add_column('order_status_history','order_id','bigint NULL')$$
CALL sc_add_column('order_status_history','from_status','varchar(255) NULL')$$
CALL sc_add_column('order_status_history','order_status','varchar(255) NULL')$$
CALL sc_add_column('order_status_history','changed_by_user_id','bigint NULL')$$
CALL sc_add_column('order_status_history','reason','varchar(255) NULL')$$
CALL sc_add_column('order_status_history','changed_at','datetime(6) NULL')$$
CALL sc_add_column('orders','user_id','bigint NULL')$$
CALL sc_add_column('orders','profile_id','bigint NULL')$$
CALL sc_add_column('orders','payment_id','bigint NULL')$$
CALL sc_add_column('orders','coupon_id','bigint NULL')$$
CALL sc_add_column('orders','order_no','varchar(255) NULL')$$
CALL sc_add_column('orders','idempotency_key','varchar(255) NULL')$$
CALL sc_add_column('orders','request_hash','varchar(255) NULL')$$
CALL sc_add_column('orders','currency','varchar(255) NULL')$$
CALL sc_add_column('orders','order_date','date NULL')$$
CALL sc_add_column('orders','subtotal','decimal(18,2) NULL')$$
CALL sc_add_column('orders','shipping_fee','decimal(18,2) NULL')$$
CALL sc_add_column('orders','discount_amount','decimal(18,2) NULL')$$
CALL sc_add_column('orders','total_amount','decimal(18,2) NULL')$$
CALL sc_add_column('orders','order_status','varchar(255) NULL')$$
CALL sc_add_column('orders','created_at','datetime(6) NULL')$$
CALL sc_add_column('orders','updated_at','datetime(6) NULL')$$
CALL sc_add_column('orders','version','bigint NULL DEFAULT 0')$$
CALL sc_add_column('outbox_events','event_id','varchar(255) NULL')$$
CALL sc_add_column('outbox_events','aggregate_type','varchar(255) NULL')$$
CALL sc_add_column('outbox_events','aggregate_id','varchar(255) NULL')$$
CALL sc_add_column('outbox_events','event_type','varchar(255) NULL')$$
CALL sc_add_column('outbox_events','aggregate_version','bigint NULL')$$
CALL sc_add_column('outbox_events','payload','text NULL')$$
CALL sc_add_column('outbox_events','correlation_id','varchar(255) NULL')$$
CALL sc_add_column('outbox_events','occurred_at','timestamp(6) NULL')$$
CALL sc_add_column('outbox_events','published_at','timestamp(6) NULL')$$
CALL sc_add_column('outbox_events','attempts','int NULL')$$
CALL sc_add_column('outbox_events','last_error','text NULL')$$
CALL sc_add_column('outbox_events','created_at','timestamp(6) NULL')$$
CALL sc_add_column('outbox_events','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('password_reset_otp','user_id','bigint NULL')$$
CALL sc_add_column('password_reset_otp','otp','varchar(255) NULL')$$
CALL sc_add_column('password_reset_otp','created_at','datetime(6) NULL')$$
CALL sc_add_column('password_reset_otp','expired_at','datetime(6) NULL')$$
CALL sc_add_column('password_reset_otp','used','bit(1) NULL')$$
CALL sc_add_column('payment_transactions','payment_id','bigint NULL')$$
CALL sc_add_column('payment_transactions','provider','varchar(255) NULL')$$
CALL sc_add_column('payment_transactions','provider_event_id','varchar(255) NULL')$$
CALL sc_add_column('payment_transactions','provider_ref','varchar(255) NULL')$$
CALL sc_add_column('payment_transactions','amount','decimal(18,2) NULL')$$
CALL sc_add_column('payment_transactions','currency','varchar(255) NULL')$$
CALL sc_add_column('payment_transactions','status','varchar(255) NULL')$$
CALL sc_add_column('payment_transactions','payload_hash','text NULL')$$
CALL sc_add_column('payment_transactions','received_at','timestamp(6) NULL')$$
CALL sc_add_column('payment_transactions','created_at','timestamp(6) NULL')$$
CALL sc_add_column('payment_transactions','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('payments','order_id','bigint NULL')$$
CALL sc_add_column('payments','provider','varchar(255) NULL')$$
CALL sc_add_column('payments','provider_ref','varchar(255) NULL')$$
CALL sc_add_column('payments','payment_method','varchar(255) NULL')$$
CALL sc_add_column('payments','payment_status','varchar(255) NULL')$$
CALL sc_add_column('payments','vnp_txn_ref','varchar(255) NULL')$$
CALL sc_add_column('payments','amount','bigint NULL')$$
CALL sc_add_column('payments','currency','varchar(255) NULL')$$
CALL sc_add_column('payments','order_info','varchar(255) NULL')$$
CALL sc_add_column('payments','bank_code','varchar(255) NULL')$$
CALL sc_add_column('payments','response_code','varchar(255) NULL')$$
CALL sc_add_column('payments','status','varchar(255) NULL')$$
CALL sc_add_column('payments','payment_date','datetime(6) NULL')$$
CALL sc_add_column('payments','expired_at','datetime(6) NULL')$$
CALL sc_add_column('payments','transaction_no','varchar(255) NULL')$$
CALL sc_add_column('payments','card_type','varchar(255) NULL')$$
CALL sc_add_column('permissions','code','varchar(255) NULL')$$
CALL sc_add_column('permissions','resource','varchar(255) NULL')$$
CALL sc_add_column('permissions','action','varchar(255) NULL')$$
CALL sc_add_column('permissions','scope','varchar(255) NULL')$$
CALL sc_add_column('permissions','description','text NULL')$$
CALL sc_add_column('permissions','created_at','timestamp(6) NULL')$$
CALL sc_add_column('permissions','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('product_embeddings','product_id','bigint NULL')$$
CALL sc_add_column('product_embeddings','variant_id','bigint NULL')$$
CALL sc_add_column('product_embeddings','model','varchar(255) NULL')$$
CALL sc_add_column('product_embeddings','vector_reference','varchar(255) NULL')$$
CALL sc_add_column('product_embeddings','source_hash','varchar(255) NULL')$$
CALL sc_add_column('product_embeddings','status','varchar(255) NULL')$$
CALL sc_add_column('product_embeddings','created_at','timestamp(6) NULL')$$
CALL sc_add_column('product_embeddings','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('product_variants','product_id','bigint NULL')$$
CALL sc_add_column('product_variants','sku','varchar(255) NULL')$$
CALL sc_add_column('product_variants','attributes_json','text NULL')$$
CALL sc_add_column('product_variants','price','decimal(18,2) NULL')$$
CALL sc_add_column('product_variants','special_price','decimal(18,2) NULL')$$
CALL sc_add_column('product_variants','currency','varchar(255) NULL')$$
CALL sc_add_column('product_variants','status','varchar(255) NULL')$$
CALL sc_add_column('product_variants','version','bigint NULL DEFAULT 0')$$
CALL sc_add_column('product_variants','created_at','timestamp(6) NULL')$$
CALL sc_add_column('product_variants','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('products','shop_id','bigint NULL')$$
CALL sc_add_column('products','category_id','bigint NULL')$$
CALL sc_add_column('products','product_name','varchar(255) NULL')$$
CALL sc_add_column('products','description','text NULL')$$
CALL sc_add_column('products','price','decimal(18,2) NULL')$$
CALL sc_add_column('products','special_price','decimal(18,2) NULL')$$
CALL sc_add_column('products','discount','decimal(18,2) NULL')$$
CALL sc_add_column('products','bio','text NULL')$$
CALL sc_add_column('products','slug','varchar(255) NULL')$$
CALL sc_add_column('products','height','double NULL')$$
CALL sc_add_column('products','length','double NULL')$$
CALL sc_add_column('products','weight','double NULL')$$
CALL sc_add_column('products','width','double NULL')$$
CALL sc_add_column('products','origin','varchar(255) NULL')$$
CALL sc_add_column('products','sold_count','int NULL')$$
CALL sc_add_column('products','review_count','int NULL')$$
CALL sc_add_column('products','avg_rating','double NULL')$$
CALL sc_add_column('products','status','varchar(255) NULL')$$
CALL sc_add_column('products','create_at','date NULL')$$
CALL sc_add_column('products','update_at','date NULL')$$
CALL sc_add_column('products','version','bigint NULL DEFAULT 0')$$
CALL sc_add_column('refresh_tokens','user_id','bigint NULL')$$
CALL sc_add_column('refresh_tokens','token_hash','varchar(255) NULL')$$
CALL sc_add_column('refresh_tokens','family_id','varchar(255) NULL')$$
CALL sc_add_column('refresh_tokens','expires_at','timestamp(6) NULL')$$
CALL sc_add_column('refresh_tokens','revoked_at','timestamp(6) NULL')$$
CALL sc_add_column('refresh_tokens','replaced_by_token_id','bigint NULL')$$
CALL sc_add_column('refresh_tokens','created_at','timestamp(6) NULL')$$
CALL sc_add_column('refresh_tokens','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('refunds','payment_id','bigint NULL')$$
CALL sc_add_column('refunds','order_id','bigint NULL')$$
CALL sc_add_column('refunds','return_request_id','bigint NULL')$$
CALL sc_add_column('refunds','amount','decimal(18,2) NULL')$$
CALL sc_add_column('refunds','currency','varchar(255) NULL')$$
CALL sc_add_column('refunds','status','varchar(255) NULL')$$
CALL sc_add_column('refunds','reason','text NULL')$$
CALL sc_add_column('refunds','idempotency_key','varchar(255) NULL')$$
CALL sc_add_column('refunds','actor_id','bigint NULL')$$
CALL sc_add_column('refunds','version','bigint NULL DEFAULT 0')$$
CALL sc_add_column('refunds','created_at','timestamp(6) NULL')$$
CALL sc_add_column('refunds','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('return_items','return_request_id','bigint NULL')$$
CALL sc_add_column('return_items','order_item_id','bigint NULL')$$
CALL sc_add_column('return_items','quantity','int NULL')$$
CALL sc_add_column('return_items','reason','text NULL')$$
CALL sc_add_column('return_items','requested_refund_amount','decimal(18,2) NULL')$$
CALL sc_add_column('return_items','created_at','timestamp(6) NULL')$$
CALL sc_add_column('return_items','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('return_requests','order_id','bigint NULL')$$
CALL sc_add_column('return_requests','user_id','bigint NULL')$$
CALL sc_add_column('return_requests','shop_id','bigint NULL')$$
CALL sc_add_column('return_requests','status','varchar(255) NULL')$$
CALL sc_add_column('return_requests','reason','text NULL')$$
CALL sc_add_column('return_requests','reviewed_by_user_id','bigint NULL')$$
CALL sc_add_column('return_requests','reviewed_at','timestamp(6) NULL')$$
CALL sc_add_column('return_requests','version','bigint NULL DEFAULT 0')$$
CALL sc_add_column('return_requests','created_at','timestamp(6) NULL')$$
CALL sc_add_column('return_requests','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('reviews','product_id','bigint NULL')$$
CALL sc_add_column('reviews','profile_id','bigint NULL')$$
CALL sc_add_column('reviews','user_id','bigint NULL')$$
CALL sc_add_column('reviews','order_id','bigint NULL')$$
CALL sc_add_column('reviews','order_item_id','bigint NULL')$$
CALL sc_add_column('reviews','customer_name','varchar(255) NULL')$$
CALL sc_add_column('reviews','rating','int NULL')$$
CALL sc_add_column('reviews','comment','varchar(255) NULL')$$
CALL sc_add_column('reviews','status','varchar(255) NULL')$$
CALL sc_add_column('reviews','created_at','datetime(6) NULL')$$
CALL sc_add_column('reviews','deleted','bit(1) NULL')$$
CALL sc_add_column('revoked_tokens','token','text NULL')$$
CALL sc_add_column('revoked_tokens','revoked_at','timestamp(6) NULL')$$
CALL sc_add_column('revoked_tokens','expires_at','timestamp(6) NULL')$$
CALL sc_add_column('role','description','text NULL')$$
CALL sc_add_column('role_permissions','role_name','varchar(255) NULL')$$
CALL sc_add_column('role_permissions','permission_id','bigint NULL')$$
CALL sc_add_column('role_permissions','created_at','timestamp(6) NULL')$$
CALL sc_add_column('role_permissions','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('search_documents','product_id','bigint NULL')$$
CALL sc_add_column('search_documents','variant_id','bigint NULL')$$
CALL sc_add_column('search_documents','source_hash','varchar(255) NULL')$$
CALL sc_add_column('search_documents','content','text NULL')$$
CALL sc_add_column('search_documents','status','varchar(255) NULL')$$
CALL sc_add_column('search_documents','indexed_at','timestamp(6) NULL')$$
CALL sc_add_column('search_documents','created_at','timestamp(6) NULL')$$
CALL sc_add_column('search_documents','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('seller_orders','order_id','bigint NULL')$$
CALL sc_add_column('seller_orders','shop_id','bigint NULL')$$
CALL sc_add_column('seller_orders','status','varchar(255) NULL')$$
CALL sc_add_column('seller_orders','subtotal','decimal(18,2) NULL')$$
CALL sc_add_column('seller_orders','shipping_fee','decimal(18,2) NULL')$$
CALL sc_add_column('seller_orders','discount_total','decimal(18,2) NULL')$$
CALL sc_add_column('seller_orders','grand_total','decimal(18,2) NULL')$$
CALL sc_add_column('seller_orders','version','bigint NULL DEFAULT 0')$$
CALL sc_add_column('seller_orders','created_at','timestamp(6) NULL')$$
CALL sc_add_column('seller_orders','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('shipments','seller_order_id','bigint NULL')$$
CALL sc_add_column('shipments','shop_id','bigint NULL')$$
CALL sc_add_column('shipments','warehouse_id','bigint NULL')$$
CALL sc_add_column('shipments','assigned_shipper_id','bigint NULL')$$
CALL sc_add_column('shipments','tracking_no','varchar(255) NULL')$$
CALL sc_add_column('shipments','carrier','varchar(255) NULL')$$
CALL sc_add_column('shipments','status','varchar(255) NULL')$$
CALL sc_add_column('shipments','shipped_at','timestamp(6) NULL')$$
CALL sc_add_column('shipments','delivered_at','timestamp(6) NULL')$$
CALL sc_add_column('shipments','version','bigint NULL DEFAULT 0')$$
CALL sc_add_column('shipments','created_at','timestamp(6) NULL')$$
CALL sc_add_column('shipments','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('shop_members','shop_id','bigint NULL')$$
CALL sc_add_column('shop_members','user_id','bigint NULL')$$
CALL sc_add_column('shop_members','role','varchar(255) NULL')$$
CALL sc_add_column('shop_members','status','varchar(255) NULL')$$
CALL sc_add_column('shop_members','created_at','timestamp(6) NULL')$$
CALL sc_add_column('shop_members','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('shops','owner_user_id','bigint NULL')$$
CALL sc_add_column('shops','name','varchar(255) NULL')$$
CALL sc_add_column('shops','slug','varchar(255) NULL')$$
CALL sc_add_column('shops','description','text NULL')$$
CALL sc_add_column('shops','status','varchar(255) NULL')$$
CALL sc_add_column('shops','approved_by_user_id','bigint NULL')$$
CALL sc_add_column('shops','approved_at','timestamp(6) NULL')$$
CALL sc_add_column('shops','created_at','timestamp(6) NULL')$$
CALL sc_add_column('shops','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('stock_movements','variant_id','bigint NULL')$$
CALL sc_add_column('stock_movements','product_id','bigint NULL')$$
CALL sc_add_column('stock_movements','quantity_delta','int NULL')$$
CALL sc_add_column('stock_movements','reason','text NULL')$$
CALL sc_add_column('stock_movements','ref_type','varchar(255) NULL')$$
CALL sc_add_column('stock_movements','ref_id','bigint NULL')$$
CALL sc_add_column('stock_movements','actor_id','bigint NULL')$$
CALL sc_add_column('stock_movements','created_at','timestamp(6) NULL')$$
CALL sc_add_column('stock_movements','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('stock_reservations','order_id','bigint NULL')$$
CALL sc_add_column('stock_reservations','variant_id','bigint NULL')$$
CALL sc_add_column('stock_reservations','product_id','bigint NULL')$$
CALL sc_add_column('stock_reservations','quantity','int NULL')$$
CALL sc_add_column('stock_reservations','status','varchar(255) NULL')$$
CALL sc_add_column('stock_reservations','expires_at','timestamp(6) NULL')$$
CALL sc_add_column('stock_reservations','version','bigint NULL DEFAULT 0')$$
CALL sc_add_column('stock_reservations','created_at','timestamp(6) NULL')$$
CALL sc_add_column('stock_reservations','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('support_tickets','user_id','bigint NULL')$$
CALL sc_add_column('support_tickets','order_id','bigint NULL')$$
CALL sc_add_column('support_tickets','assigned_user_id','bigint NULL')$$
CALL sc_add_column('support_tickets','subject','varchar(255) NULL')$$
CALL sc_add_column('support_tickets','description','text NULL')$$
CALL sc_add_column('support_tickets','status','varchar(255) NULL')$$
CALL sc_add_column('support_tickets','priority','varchar(255) NULL')$$
CALL sc_add_column('support_tickets','created_at','timestamp(6) NULL')$$
CALL sc_add_column('support_tickets','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('tracking_events','shipment_id','bigint NULL')$$
CALL sc_add_column('tracking_events','status','varchar(255) NULL')$$
CALL sc_add_column('tracking_events','description','text NULL')$$
CALL sc_add_column('tracking_events','location','varchar(255) NULL')$$
CALL sc_add_column('tracking_events','actor_id','bigint NULL')$$
CALL sc_add_column('tracking_events','occurred_at','timestamp(6) NULL')$$
CALL sc_add_column('tracking_events','created_at','timestamp(6) NULL')$$
CALL sc_add_column('tracking_events','updated_at','timestamp(6) NULL')$$
CALL sc_add_column('user','password','varchar(255) NULL')$$
CALL sc_add_column('user','email','varchar(255) NULL')$$
CALL sc_add_column('user','enabled','bit(1) NULL')$$
CALL sc_add_column('user','status','varchar(255) NULL')$$
CALL sc_add_column('user','locked_reason','varchar(255) NULL')$$
CALL sc_add_column('user','locked_at','datetime(6) NULL')$$
CALL sc_add_column('user','created_at','datetime(6) NULL')$$
CALL sc_add_column('user_profile','user_id','bigint NULL')$$
CALL sc_add_column('user_profile','username','varchar(255) NULL')$$
CALL sc_add_column('user_profile','gender','bit(1) NULL')$$
CALL sc_add_column('user_profile','birth_date','date NULL')$$
CALL sc_add_column('user_profile','mobile_number','varchar(255) NULL')$$
CALL sc_add_column('user_role','user_id','bigint NULL')$$
CALL sc_add_column('user_role','role_name','varchar(255) NULL')$$
CALL sc_add_column('warehouses','shop_id','bigint NULL')$$
CALL sc_add_column('warehouses','name','varchar(255) NULL')$$
CALL sc_add_column('warehouses','address_id','bigint NULL')$$
CALL sc_add_column('warehouses','status','varchar(255) NULL')$$
CALL sc_add_column('warehouses','created_at','timestamp(6) NULL')$$
CALL sc_add_column('warehouses','updated_at','timestamp(6) NULL')$$
DROP PROCEDURE sc_add_column$$

DROP PROCEDURE IF EXISTS sc_relax_columns$$
CREATE PROCEDURE sc_relax_columns()
BEGIN
  DECLARE done INT DEFAULT 0;
  DECLARE t VARCHAR(255); DECLARE c VARCHAR(255); DECLARE typ TEXT; DECLARE dt VARCHAR(64);
  DECLARE charset_name VARCHAR(64); DECLARE collation_name VARCHAR(64); DECLARE def TEXT; DECLARE extra_text TEXT;
  DECLARE cur CURSOR FOR SELECT TABLE_NAME,COLUMN_NAME,COLUMN_TYPE,DATA_TYPE,CHARACTER_SET_NAME,COLLATION_NAME,COLUMN_DEFAULT,EXTRA
    FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME IN ('addresses','ai_conversations','ai_messages','audit_logs','cart_items','carts','categories','checkout_quotes','consumer_inbox','coupon_categories','coupon_products','coupon_redemptions','coupons','disputes','flash_sale_campaigns','flash_sale_items','flash_sale_purchases','idempotency_records','image','inventory','notification_templates','notifications','order_delivery_addresses','order_items','order_status_history','orders','outbox_events','password_reset_otp','payment_transactions','payments','permissions','product_embeddings','product_variants','products','refresh_tokens','refunds','return_items','return_requests','reviews','revoked_tokens','role','role_permissions','search_documents','seller_orders','shipments','shop_members','shops','stock_movements','stock_reservations','support_tickets','tracking_events','user','user_profile','user_role','warehouses')
    AND COLUMN_KEY <> 'PRI' AND (GENERATION_EXPRESSION IS NULL OR GENERATION_EXPRESSION='');
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
  OPEN cur;
  read_loop: LOOP
    FETCH cur INTO t,c,typ,dt,charset_name,collation_name,def,extra_text; IF done THEN LEAVE read_loop; END IF;
    IF dt='enum' OR dt='set' THEN SET typ='varchar(255)'; END IF;
    SET @sc_sql=CONCAT('ALTER TABLE `',t,'` MODIFY COLUMN `',c,'` ',typ,
      IF(charset_name IS NULL,'',CONCAT(' CHARACTER SET ',charset_name,' COLLATE ',collation_name)), ' NULL',
      CASE WHEN def IS NULL THEN ' DEFAULT NULL' WHEN extra_text LIKE '%DEFAULT_GENERATED%' OR (dt='bit' AND def REGEXP '^b') THEN CONCAT(' DEFAULT ',def) ELSE CONCAT(' DEFAULT ',QUOTE(def)) END,
      IF(LOCATE('on update',LOWER(extra_text))>0,CONCAT(' ',SUBSTRING(extra_text,LOCATE('on update',LOWER(extra_text)))),''));
    PREPARE sc_stmt FROM @sc_sql; EXECUTE sc_stmt; DEALLOCATE PREPARE sc_stmt;
  END LOOP;
  CLOSE cur;
END$$
CALL sc_relax_columns()$$
DROP PROCEDURE sc_relax_columns$$

DROP PROCEDURE IF EXISTS sc_add_index$$
CREATE PROCEDURE sc_add_index(IN t VARCHAR(255), IN idx VARCHAR(255), IN col VARCHAR(255))
BEGIN
  IF NOT EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=t AND INDEX_NAME=idx) THEN
    SET @sc_sql=CONCAT('CREATE INDEX `',idx,'` ON `',t,'` (`',col,'`)');
    PREPARE sc_stmt FROM @sc_sql; EXECUTE sc_stmt; DEALLOCATE PREPARE sc_stmt;
  END IF;
END$$
CALL sc_add_index('addresses','idx_addresses_profile_id','profile_id')$$
CALL sc_add_index('ai_conversations','idx_ai_conversations_user_id','user_id')$$
CALL sc_add_index('ai_messages','idx_ai_messages_conversation_id','conversation_id')$$
CALL sc_add_index('audit_logs','idx_audit_logs_actor_id','actor_id')$$
CALL sc_add_index('audit_logs','idx_audit_logs_resource_id','resource_id')$$
CALL sc_add_index('audit_logs','idx_audit_logs_correlation_id','correlation_id')$$
CALL sc_add_index('cart_items','idx_cart_items_cart_id','cart_id')$$
CALL sc_add_index('cart_items','idx_cart_items_product_id','product_id')$$
CALL sc_add_index('cart_items','idx_cart_items_variant_id','variant_id')$$
CALL sc_add_index('carts','idx_carts_profile_id','profile_id')$$
CALL sc_add_index('carts','idx_carts_user_id','user_id')$$
CALL sc_add_index('categories','idx_categories_parent_id','parent_id')$$
CALL sc_add_index('categories','idx_categories_slug','slug')$$
CALL sc_add_index('checkout_quotes','idx_checkout_quotes_user_id','user_id')$$
CALL sc_add_index('checkout_quotes','idx_checkout_quotes_address_id','address_id')$$
CALL sc_add_index('consumer_inbox','idx_consumer_inbox_event_id','event_id')$$
CALL sc_add_index('coupon_categories','idx_coupon_categories_coupon_id','coupon_id')$$
CALL sc_add_index('coupon_categories','idx_coupon_categories_category_id','category_id')$$
CALL sc_add_index('coupon_products','idx_coupon_products_coupon_id','coupon_id')$$
CALL sc_add_index('coupon_products','idx_coupon_products_product_id','product_id')$$
CALL sc_add_index('coupon_redemptions','idx_coupon_redemptions_coupon_id','coupon_id')$$
CALL sc_add_index('coupon_redemptions','idx_coupon_redemptions_user_id','user_id')$$
CALL sc_add_index('coupon_redemptions','idx_coupon_redemptions_order_id','order_id')$$
CALL sc_add_index('coupon_redemptions','idx_coupon_redemptions_shop_id','shop_id')$$
CALL sc_add_index('coupons','idx_coupons_shop_id','shop_id')$$
CALL sc_add_index('coupons','idx_coupons_code','code')$$
CALL sc_add_index('disputes','idx_disputes_order_id','order_id')$$
CALL sc_add_index('disputes','idx_disputes_seller_order_id','seller_order_id')$$
CALL sc_add_index('disputes','idx_disputes_return_request_id','return_request_id')$$
CALL sc_add_index('disputes','idx_disputes_opened_by_user_id','opened_by_user_id')$$
CALL sc_add_index('disputes','idx_disputes_assigned_user_id','assigned_user_id')$$
CALL sc_add_index('flash_sale_items','idx_flash_sale_items_campaign_id','campaign_id')$$
CALL sc_add_index('flash_sale_items','idx_flash_sale_items_variant_id','variant_id')$$
CALL sc_add_index('flash_sale_items','idx_flash_sale_items_shop_id','shop_id')$$
CALL sc_add_index('flash_sale_purchases','idx_flash_sale_purchases_campaign_id','campaign_id')$$
CALL sc_add_index('flash_sale_purchases','idx_flash_sale_purchases_flash_sale_item_id','flash_sale_item_id')$$
CALL sc_add_index('flash_sale_purchases','idx_flash_sale_purchases_user_id','user_id')$$
CALL sc_add_index('flash_sale_purchases','idx_flash_sale_purchases_order_id','order_id')$$
CALL sc_add_index('idempotency_records','idx_idempotency_records_user_id','user_id')$$
CALL sc_add_index('idempotency_records','idx_idempotency_records_idempotency_key','idempotency_key')$$
CALL sc_add_index('image','idx_image_product_id','product_id')$$
CALL sc_add_index('image','idx_image_variant_id','variant_id')$$
CALL sc_add_index('inventory','idx_inventory_product_id','product_id')$$
CALL sc_add_index('inventory','idx_inventory_variant_id','variant_id')$$
CALL sc_add_index('notification_templates','idx_notification_templates_code','code')$$
CALL sc_add_index('notifications','idx_notifications_user_id','user_id')$$
CALL sc_add_index('notifications','idx_notifications_event_id','event_id')$$
CALL sc_add_index('order_delivery_addresses','idx_order_delivery_addresses_order_id','order_id')$$
CALL sc_add_index('order_items','idx_order_items_order_id','order_id')$$
CALL sc_add_index('order_items','idx_order_items_seller_order_id','seller_order_id')$$
CALL sc_add_index('order_items','idx_order_items_product_id','product_id')$$
CALL sc_add_index('order_items','idx_order_items_variant_id','variant_id')$$
CALL sc_add_index('order_items','idx_order_items_cart_item_id','cart_item_id')$$
CALL sc_add_index('order_status_history','idx_order_status_history_order_id','order_id')$$
CALL sc_add_index('order_status_history','idx_order_status_history_changed_by_user_id','changed_by_user_id')$$
CALL sc_add_index('orders','idx_orders_user_id','user_id')$$
CALL sc_add_index('orders','idx_orders_profile_id','profile_id')$$
CALL sc_add_index('orders','idx_orders_payment_id','payment_id')$$
CALL sc_add_index('orders','idx_orders_coupon_id','coupon_id')$$
CALL sc_add_index('orders','idx_orders_idempotency_key','idempotency_key')$$
CALL sc_add_index('outbox_events','idx_outbox_events_event_id','event_id')$$
CALL sc_add_index('outbox_events','idx_outbox_events_aggregate_id','aggregate_id')$$
CALL sc_add_index('outbox_events','idx_outbox_events_correlation_id','correlation_id')$$
CALL sc_add_index('password_reset_otp','idx_password_reset_otp_user_id','user_id')$$
CALL sc_add_index('payment_transactions','idx_payment_transactions_payment_id','payment_id')$$
CALL sc_add_index('payment_transactions','idx_payment_transactions_provider_event_id','provider_event_id')$$
CALL sc_add_index('payments','idx_payments_order_id','order_id')$$
CALL sc_add_index('permissions','idx_permissions_code','code')$$
CALL sc_add_index('product_embeddings','idx_product_embeddings_product_id','product_id')$$
CALL sc_add_index('product_embeddings','idx_product_embeddings_variant_id','variant_id')$$
CALL sc_add_index('product_variants','idx_product_variants_product_id','product_id')$$
CALL sc_add_index('products','idx_products_shop_id','shop_id')$$
CALL sc_add_index('products','idx_products_category_id','category_id')$$
CALL sc_add_index('products','idx_products_slug','slug')$$
CALL sc_add_index('refresh_tokens','idx_refresh_tokens_user_id','user_id')$$
CALL sc_add_index('refresh_tokens','idx_refresh_tokens_family_id','family_id')$$
CALL sc_add_index('refresh_tokens','idx_refresh_tokens_replaced_by_token_id','replaced_by_token_id')$$
CALL sc_add_index('refunds','idx_refunds_payment_id','payment_id')$$
CALL sc_add_index('refunds','idx_refunds_order_id','order_id')$$
CALL sc_add_index('refunds','idx_refunds_return_request_id','return_request_id')$$
CALL sc_add_index('refunds','idx_refunds_idempotency_key','idempotency_key')$$
CALL sc_add_index('refunds','idx_refunds_actor_id','actor_id')$$
CALL sc_add_index('return_items','idx_return_items_return_request_id','return_request_id')$$
CALL sc_add_index('return_items','idx_return_items_order_item_id','order_item_id')$$
CALL sc_add_index('return_requests','idx_return_requests_order_id','order_id')$$
CALL sc_add_index('return_requests','idx_return_requests_user_id','user_id')$$
CALL sc_add_index('return_requests','idx_return_requests_shop_id','shop_id')$$
CALL sc_add_index('return_requests','idx_return_requests_reviewed_by_user_id','reviewed_by_user_id')$$
CALL sc_add_index('reviews','idx_reviews_product_id','product_id')$$
CALL sc_add_index('reviews','idx_reviews_profile_id','profile_id')$$
CALL sc_add_index('reviews','idx_reviews_user_id','user_id')$$
CALL sc_add_index('reviews','idx_reviews_order_id','order_id')$$
CALL sc_add_index('reviews','idx_reviews_order_item_id','order_item_id')$$
CALL sc_add_index('role_permissions','idx_role_permissions_permission_id','permission_id')$$
CALL sc_add_index('search_documents','idx_search_documents_product_id','product_id')$$
CALL sc_add_index('search_documents','idx_search_documents_variant_id','variant_id')$$
CALL sc_add_index('seller_orders','idx_seller_orders_order_id','order_id')$$
CALL sc_add_index('seller_orders','idx_seller_orders_shop_id','shop_id')$$
CALL sc_add_index('shipments','idx_shipments_seller_order_id','seller_order_id')$$
CALL sc_add_index('shipments','idx_shipments_shop_id','shop_id')$$
CALL sc_add_index('shipments','idx_shipments_warehouse_id','warehouse_id')$$
CALL sc_add_index('shipments','idx_shipments_assigned_shipper_id','assigned_shipper_id')$$
CALL sc_add_index('shop_members','idx_shop_members_shop_id','shop_id')$$
CALL sc_add_index('shop_members','idx_shop_members_user_id','user_id')$$
CALL sc_add_index('shops','idx_shops_owner_user_id','owner_user_id')$$
CALL sc_add_index('shops','idx_shops_slug','slug')$$
CALL sc_add_index('shops','idx_shops_approved_by_user_id','approved_by_user_id')$$
CALL sc_add_index('stock_movements','idx_stock_movements_variant_id','variant_id')$$
CALL sc_add_index('stock_movements','idx_stock_movements_product_id','product_id')$$
CALL sc_add_index('stock_movements','idx_stock_movements_ref_id','ref_id')$$
CALL sc_add_index('stock_movements','idx_stock_movements_actor_id','actor_id')$$
CALL sc_add_index('stock_reservations','idx_stock_reservations_order_id','order_id')$$
CALL sc_add_index('stock_reservations','idx_stock_reservations_variant_id','variant_id')$$
CALL sc_add_index('stock_reservations','idx_stock_reservations_product_id','product_id')$$
CALL sc_add_index('support_tickets','idx_support_tickets_user_id','user_id')$$
CALL sc_add_index('support_tickets','idx_support_tickets_order_id','order_id')$$
CALL sc_add_index('support_tickets','idx_support_tickets_assigned_user_id','assigned_user_id')$$
CALL sc_add_index('tracking_events','idx_tracking_events_shipment_id','shipment_id')$$
CALL sc_add_index('tracking_events','idx_tracking_events_actor_id','actor_id')$$
CALL sc_add_index('user','idx_user_email','email')$$
CALL sc_add_index('user_profile','idx_user_profile_user_id','user_id')$$
CALL sc_add_index('user_role','idx_user_role_user_id','user_id')$$
CALL sc_add_index('warehouses','idx_warehouses_shop_id','shop_id')$$
CALL sc_add_index('warehouses','idx_warehouses_address_id','address_id')$$
DROP PROCEDURE sc_add_index$$
DELIMITER ;

-- Preserve existing inverse links and historical snapshots.
UPDATE payments p JOIN orders o ON o.payment_id=p.payment_id SET p.order_id=o.order_id WHERE p.order_id IS NULL;
UPDATE orders o JOIN user_profile up ON up.profile_id=o.profile_id SET o.user_id=up.user_id WHERE o.user_id IS NULL;
UPDATE carts c JOIN user_profile up ON up.profile_id=c.profile_id SET c.user_id=up.user_id WHERE c.user_id IS NULL;
UPDATE order_items oi JOIN products p ON p.product_id=oi.product_id SET oi.product_title=p.product_name WHERE oi.product_title IS NULL;
UPDATE `flash_sale_items` SET version=0 WHERE version IS NULL;
UPDATE `inventory` SET version=0 WHERE version IS NULL;
UPDATE `orders` SET version=0 WHERE version IS NULL;
UPDATE `product_variants` SET version=0 WHERE version IS NULL;
UPDATE `products` SET version=0 WHERE version IS NULL;
UPDATE `refunds` SET version=0 WHERE version IS NULL;
UPDATE `return_requests` SET version=0 WHERE version IS NULL;
UPDATE `seller_orders` SET version=0 WHERE version IS NULL;
UPDATE `shipments` SET version=0 WHERE version IS NULL;
UPDATE `stock_reservations` SET version=0 WHERE version IS NULL;
