# MyShop backend

Java 17, Spring Boot, MySQL, Redis, MinIO.

## Configuration

Copy .env.example to .env and replace all placeholders. Keep secrets out of Git.
Local execution loads .env and .env.dev as Java properties (do not use shell export syntax).
Set SPRING_PROFILES_ACTIVE externally to select another profile; default is dev.
Use literal values in dotenv files; Java properties imports do not expand shell-style variables.
For local development, provide MySQL, Redis and MinIO at the configured addresses.
Run: ./mvnw spring-boot:run (Windows: .\mvnw.cmd spring-boot:run).

## Production / Docker

Configure .env.prod with production database settings, CORS_ALLOWED_ORIGINS (comma-separated exact origins),
FRONTEND_PAYMENT_RETURN_URL, MINIO_PUBLIC_URL (browser-accessible HTTPS endpoint),
COOKIE_SECURE=true and COOKIE_SAME_SITE=Lax. Cross-site deployments require SameSite=None and HTTPS.
Use a reverse proxy with HTTPS for backend and MinIO. Forward MinIO requests without rewriting their signed host/path/query.
The API and MinIO ports bind only to loopback; MySQL and Redis are private to the Compose network.
Run: docker compose --env-file .env --env-file .env.prod up -d --build
Compose interpolation and container env use both files; later files take precedence.
DBMS_USERNAME must be a non-root user. Set MYSQL_ROOT_PASSWORD separately.
On an existing MySQL volume, MYSQL_USER/PASSWORD do not recreate accounts: provision the user/grants manually before switching.
Production uses ddl-auto=validate. An empty database will not start the application until schema is provisioned.
Before rollout, back up the existing DB and establish reviewed Flyway/Liquibase migrations from its actual schema.
No guessed initial migration or automatic baseline is applied by this change.

Admin bootstrap is off. To create an administrator, set BOOTSTRAP_ADMIN_ENABLED=true,
BOOTSTRAP_ADMIN_EMAIL and BOOTSTRAP_ADMIN_PASSWORD (at least 12 characters), start once, then disable bootstrap.
Existing admin accounts are not changed: rotate any old default credentials manually.
JWT_EXPIRATION and JWT_REFRESHABLE_DURATION are milliseconds; cookie lifetimes use the same durations. Browser writes require an allowed Origin, or an allowed Referer for authenticated cookie requests.
API clients using cookies must supply Origin; bearer-only requests do not require it.
Swagger is disabled in production unless SWAGGER_ENABLED=true.
Readiness: /actuator/health/readiness (DB and Redis); MinIO has its own Compose healthcheck.
Generated PDF files are temporary container data; do not treat them as durable storage.

## Operations

Back up MySQL with a consistent logical dump and back up the MinIO data volume or use object replication.
Store backups outside the Docker host; periodically restore both into an isolated environment and verify orders/images.
Redis AOF is enabled; token blacklist recovery also relies on the database.
Login/Google/OTP/password-reset requests are limited in Redis to 20 per direct peer IP per endpoint per 60 seconds.
Tune app.auth.rate-limit.requests and app.auth.rate-limit.window-seconds as needed.
Behind a reverse proxy, also apply per-client limits at the proxy; the app deliberately ignores untrusted forwarded IP headers.
A production TLS proxy configuration and schema migration remain deployment work.
SMTP connection/read/write timeouts are configured. MinIO signed URL lifetime is in minutes (default 5).
Run unit tests: ./mvnw -Dtest=OrderServiceTest,CookieOriginFilterTest,AuthRateLimitFilterTest test
The contextLoads test needs live dependencies and valid environment settings.

## API paths, thông báo lỗi và MyBatis

- Đường dẫn API nằm trong constants/ApiPath.java. Controller dùng BASE_* và hằng số đường dẫn tương đối; PREFIX giữ cấu hình api.prefix.
- Gửi header locale: vi hoặc locale: en. Mặc định/giá trị không hỗ trợ dùng tiếng Việt; en-US cũng được hỗ trợ.
- Nội dung nằm trong messages.properties (mặc định Việt), messages_vi.properties và messages_en.properties, mã hóa UTF-8.
- CommonMessageUtils chứa key; ErrorCatalog giữ mã số/HTTP status cũ. Không còn enum ErrorCode.
- Ném lỗi nghiệp vụ: throw new AppException(CommonMessageUtils.CATEGORY_NOT_EXISTED).
- Có tham số: throw new AppException(CommonMessageUtils.PRODUCT_NOT_ENOUGH, requested, available).
- Trả lỗi trực tiếp: return ApiResponse.error(MessageHandlerUtils.getMessage(CommonMessageUtils.ERROR_NOT_FOUND)).
  Với REST endpoint, ưu tiên AppException để GlobalExceptionHandler đặt đúng HTTP status; ApiResponse.error chỉ tạo response body.
- Không dịch key trước khi đưa vào AppException. Dùng placeholder {0}, {1} cho message nghiệp vụ; {min}/{max} dành cho Bean Validation.
- Lỗi nghiệp vụ, validation, JSON, thiếu tham số, 401/403, Origin và rate limit đều trả thông báo theo locale.
- Các thông báo thành công hiện có được giữ nguyên.

MyBatis dùng starter 3.0.4 cho Spring Boot 3/Java 17 (https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/), dùng chung datasource với JPA.
Interface SQL đặt trong mybatis/mapper (khác package mapper của MapStruct); XML đặt trong resources/mybatis/mapper.
Ví dụ OrderReportMapper.summarizeByStatus(fromDate, toDate) tổng hợp đơn hàng theo trạng thái và khoảng ngày (bao gồm hai đầu).
Inject OrderReportMapper bằng constructor/Lombok @RequiredArgsConstructor vào service và gọi trong @Transactional(readOnly = true).
Truy vấn dùng #{parameter} để bind tham số, không nối trực tiếp dữ liệu người dùng vào SQL.
Nếu vừa ghi bằng JPA vừa đọc bằng MyBatis trong cùng transaction, flush JPA trước khi cần nhìn thấy thay đổi.
Mapper mẫu chưa thay thế logic báo cáo JPA hiện có và chưa thêm endpoint mới.

Kiểm thử không cần database:
./mvnw -o -Dtest=OrderServiceTest,CookieOriginFilterTest,AuthRateLimitFilterTest,MessageHandlerUtilsTest,LocalizedErrorResponseTest,OrderReportMapperTest -DforkCount=0 test-compile org.apache.maven.plugins:maven-surefire-plugin:3.5.4:test

## Model độc lập và chuyển schema

Entity đã bỏ toàn bộ JPA association/enum; tham chiếu lưu bằng ID thường. Xem [phân tích model](docs/SMARTCOMMERCE_MODEL_ANALYSIS.md) và [hướng dẫn migration MySQL](db/mysql/README.md). Mặc định schema mode là validate; chạy SQL create/upgrade trước khi khởi động. Không dùng Hibernate update để thay thế bước gỡ constraint cũ.
Các entity mới là nền cho tính năng trong tài liệu, chưa phải toàn bộ marketplace đã có API/service.
