package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class ProductCategoryService {
    LinkedHashMap<String, Double> hsCategories = new LinkedHashMap<>();
    public ProductCategoryService() { // constructor
        hsCategories.put("Agricultural & Edible Products", 32000.00);
        hsCategories.put("Mineral & Energy Resources", 40000.00);
        hsCategories.put("Vehicles, Defense & Consumer Goods", 95000.00);
        hsCategories.put("Chemicals, Plastics & Cosmetics", 55000.00);
        hsCategories.put("Base Metals & Metalware", 45000.00);
        hsCategories.put("Machinery, Electronics & Precision Equipment", 135000.00);
        hsCategories.put("Textiles, Apparel & Footwear", 28000.00);
        hsCategories.put("Wood, Paper & Natural Raw Materials", 22000.00);
    }

    public List<String> getAllCategoryNames() {
        return new ArrayList<>(hsCategories.keySet());
    }

    public double getCategoryValue(String categoryName) {
        Double value = hsCategories.get(categoryName);
        if (value == null) {
            throw new RuntimeException("Unknown product category: " + categoryName);
        }
        return value;
    }
}
