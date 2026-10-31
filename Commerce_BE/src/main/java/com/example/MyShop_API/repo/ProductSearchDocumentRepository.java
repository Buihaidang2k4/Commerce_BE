package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.ProductSearchDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSearchDocumentRepository extends JpaRepository<ProductSearchDocument, Long> {
}
