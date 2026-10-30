package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "categories", indexes = {@Index(name = "idx_categories_parent_id", columnList = "parent_id"), @Index(name = "idx_categories_slug", columnList = "slug")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    private Long parentId;
    private String categoryName;
    private String slug;
    @Column(columnDefinition = "TEXT")
    private String description;
    private LocalDate createAt;
    private LocalDate updateAt;

}
