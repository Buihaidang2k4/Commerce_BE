package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.SearchDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchDocumentRepository extends JpaRepository<SearchDocument, Long> {
}
