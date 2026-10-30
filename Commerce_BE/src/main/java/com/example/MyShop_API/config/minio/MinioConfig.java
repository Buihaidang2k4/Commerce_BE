package com.example.MyShop_API.config.minio;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinioConfig {
    @Value("${minio.url}")
    private String url;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(url)
                    .credentials(accessKey, secretKey)
                    .build();

            log.info("Kết nối thành công , url {}", url);
            return client;
        } catch (Exception e) {
            log.error("Có lỗi xảy ra khi kết nối tới minio {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
