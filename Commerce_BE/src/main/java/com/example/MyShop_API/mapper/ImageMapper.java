package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.ImageDTO;
import com.example.MyShop_API.entity.Image;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageDTO toImageDTO(Image image);

    List<ImageDTO> toImagesDTO(List<Image> images);
}
