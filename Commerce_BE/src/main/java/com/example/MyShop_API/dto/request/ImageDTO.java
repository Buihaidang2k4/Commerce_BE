package com.example.MyShop_API.dto.request;

import lombok.Data;

@Data
public class ImageDTO {
    private Long id;
    private String fileName;
    private String downloadUrl;
    private String url;
}
