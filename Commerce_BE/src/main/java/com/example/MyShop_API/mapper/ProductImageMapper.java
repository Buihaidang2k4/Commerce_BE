package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.ImageDTO;
import com.example.MyShop_API.entity.ProductImage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    ImageDTO toImageDTO(ProductImage image);

    List<ImageDTO> toImagesDTO(List<ProductImage> images);
}
