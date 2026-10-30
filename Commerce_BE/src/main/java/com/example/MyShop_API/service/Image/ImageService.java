package com.example.MyShop_API.service.Image;

import com.example.MyShop_API.dto.request.ImageDTO;
import com.example.MyShop_API.entity.Image;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.mapper.ImageMapper;
import com.example.MyShop_API.repo.ImageRepository;
import com.example.MyShop_API.service.product.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageService implements IImageService {
    ImageRepository imageRepository;
    ProductService productService;
    ImageMapper imageMapper;

    @Override
    @Transactional(readOnly = true)
    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElseThrow(() ->
                new AppException(CommonMessageUtils.IMAGE_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageDTO> getImageByProductId(Long productId) {
        List<Image> ds = imageRepository.findImageByProductId(productId);
        if (ds.isEmpty()) throw new AppException(CommonMessageUtils.IMAGE_NOT_FOUND);

        return ds.stream().map(imageMapper::toImageDTO).toList();
    }

    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id).ifPresentOrElse(imageRepository::delete, () -> {
            throw new AppException(CommonMessageUtils.IMAGE_NOT_FOUND, id);
        });
    }


    @Override
    public List<ImageDTO> addImages(Long productId, List<MultipartFile> files) {
        Product product = productService.getProductById(productId);
        List<ImageDTO> saveImageDTO = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Image image = Image.builder()
                        .fileName(file.getOriginalFilename())
                        .fileType(file.getContentType())
                        .image(new SerialBlob(file.getBytes()))
                        .productId(product.getProductId())
                        .build();

                Image savedImage = imageRepository.saveAndFlush(image);

                String downloadUrl = "/api/v1/images/image/download/" + savedImage.getId();
                savedImage.setDownloadUrl(downloadUrl);

                imageRepository.saveAndFlush(savedImage);

                ImageDTO imageDTO = imageMapper.toImageDTO(savedImage);
                saveImageDTO.add(imageDTO);
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return saveImageDTO;
    }

    @Override
    public void addImageUrl(Long productId, List<String> urls) {
        Product product = productService.getProductById(productId);
        for (String url : urls) {
            Image image = Image.builder()
                    .url(url)
                    .productId(product.getProductId())
                    .build();
            imageRepository.save(image);
        }
    }

    @Override
    public void updateImage(Long productId, MultipartFile file) {
        try {
            Image image = getImageById(productId);
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
