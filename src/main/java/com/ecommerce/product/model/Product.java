package com.ecommerce.product.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String slug;
    private String description;
    private double price;
    private int quantity;
    private String category;
    private String brand;
    private int sold;
    private List<String> images = new ArrayList<>();
    private String color;
    private List<String> ratings = new ArrayList<>();
    private boolean isDeleted = false;
}
