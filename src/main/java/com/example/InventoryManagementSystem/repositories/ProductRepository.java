package com.example.InventoryManagementSystem.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.InventoryManagementSystem.models.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
	
	List<Product> findByNameContainingOrDescriptionContaining(String name, String description);
	boolean existsBySku(String sku);
}
