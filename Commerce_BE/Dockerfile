# Build app
FROM eclipse-temurin:17-jdk-jammy AS builder

# tao thu muc lam viec
WORKDIR /app

# copy maven
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw
# Tải trước dependency build trước tăng tốc độ build sau này
RUN ./mvnw dependency:go-offline

# Copy source
COPY src/ src/

# bo qua test
RUN ./mvnw clean package -DskipTests

# 2 Chay voi jre nhe môi trường chỉ chạy ứng dụng không cần build
FROM eclipse-temurin:17-jre-jammy AS runtime

# Tạo user không phải root để tăng bảo mật
RUN adduser --system appuser && mkdir -p /app/data/reports /app/data/excel && chown -R appuser /app
USER root
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
USER appuser

# Gắn nhãn metadata (optional)
LABEL maintainer="dangbui623@gmail.com"
LABEL version="1.0"

WORKDIR /app

EXPOSE 8080

COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]


