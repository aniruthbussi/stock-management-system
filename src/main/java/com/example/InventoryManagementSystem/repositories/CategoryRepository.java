package com.example.InventoryManagementSystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.InventoryManagementSystem.models.Category;


public interface CategoryRepository extends JpaRepository<Category, Long>{

	
}
