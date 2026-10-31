package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPath;


import com.example.MyShop_API.dto.request.ImageDTO;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.entity.ProductImage;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.service.ProductImage.IProductImageService;
import com.example.MyShop_API.service.ProductImage.MinioService;
import io.minio.errors.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(ApiPath.BASE_IMAGE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductImageController {
    IProductImageService imageService;
    MinioService minioService;

    @PostMapping(ApiPath.IMAGE_UPLOAD_PRODUCT_PRODUCT_ID)
    ResponseEntity<ApiResponse> saveImages(@RequestPart("files") List<MultipartFile> files, @PathVariable("productId") Long productId) {
        try {
            List<ImageDTO> imageDTOs = imageService.addImages(productId, files);


            return ResponseEntity.ok(new ApiResponse(200, "Upload success!", imageDTOs));
        } catch (AppException e) {
            throw e;
        }
    }

    // Lưu nhiều URL ảnh cho product
    @PostMapping(ApiPath.IMAGE_PRODUCT_ID_IMAGES_URL)
    public ResponseEntity<ApiResponse<List<String>>> addImageUrls(
            @PathVariable("productId") Long productId,
            @RequestBody List<String> urls
    ) {
        imageService.addImageUrl(productId, urls);
        return ResponseEntity.ok(new ApiResponse<>(200, "Add image URLs successfully!", null));
    }


    @GetMapping(ApiPath.IMAGE_IMAGE_DOWNLOAD_IMAGE_ID)
    ResponseEntity<Resource> downloadImage(@PathVariable Long imageId) throws SQLException {
        ProductImage image = imageService.getImageById(imageId);
        if (image == null || image.getDownloadUrl().isEmpty()) return ResponseEntity.notFound().build();

        ByteArrayResource resource = new ByteArrayResource(image.getImage().getBytes(1, (int) image.getImage().length()));
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(image.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping(ApiPath.IMAGE_PRODUCT_PRODUCT_ID_IMAGES)
    ResponseEntity<ApiResponse> getImagesByProductId(@PathVariable Long productId) {
        List<ImageDTO> ds = imageService.getImageByProductId(productId);
        if (ds.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new ApiResponse(200, "Get success!", ds));
    }

    @PutMapping(ApiPath.IMAGE_IMAGE_IMAGE_ID_UPDATE)
    ResponseEntity<ApiResponse> updateImage(@PathVariable Long imageId, @RequestPart("file") MultipartFile file) throws SQLException, IOException {
        try {
            ProductImage image = imageService.getImageById(imageId);
            if (image != null) {
                imageService.updateImage(imageId, file);
                return ResponseEntity.ok(new ApiResponse(200, "Update success!", null));
            }
        } catch (AppException e) {
            throw e;
        }
        throw new AppException(com.example.MyShop_API.constants.CommonMessageUtils.IMAGE_NOT_FOUND);
    }

    @DeleteMapping(ApiPath.IMAGE_IMAGE_IMAGE_ID_DELETE)
    ResponseEntity<ApiResponse> deleteImage(@PathVariable Long imageId) {
        try {
            ProductImage image = imageService.getImageById(imageId);
            if (image != null) {
                imageService.deleteImageById(imageId);
                return ResponseEntity.ok(new ApiResponse(200, "Delete success!", null));
            }
        } catch (AppException e) {
            throw e;
        }
        throw new AppException(com.example.MyShop_API.constants.CommonMessageUtils.IMAGE_NOT_FOUND);
    }

    @PostMapping(ApiPath.IMAGE_PUT_OBJECT_IMAGE)
    ResponseEntity<ApiResponse<String>> saveImages(@RequestPart("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(new ApiResponse(200, "Upload success!", minioService.putObject(file)));
        } catch (AppException | ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            if (e instanceof AppException appException) throw appException;
            throw new IllegalStateException(e);
        }
    }

    @DeleteMapping(ApiPath.IMAGE_DELETE_OBJECT_IMAGE)
    ResponseEntity<ApiResponse<Void>> deleteImage(@RequestParam("objectName") String objectName) {
        try {
            minioService.removeObject(objectName);
            return ResponseEntity.ok(new ApiResponse(200, "Delete success!", null));
        } catch (AppException | ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            if (e instanceof AppException appException) throw appException;
            throw new IllegalStateException(e);
        }
    }


    @GetMapping(ApiPath.IMAGE_GET_OBJECT_IMAGE)
    ResponseEntity<ApiResponse<String>> downloadImage(@RequestParam("objectName") String objectName) throws SQLException {
        try {
            return ResponseEntity.ok(new ApiResponse<>(200, "success", minioService.presignedUrl(objectName)));
        } catch (AppException | ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            if (e instanceof AppException appException) throw appException;
            throw new IllegalStateException(e);
        }
    }


}
