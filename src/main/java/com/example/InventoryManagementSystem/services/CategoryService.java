package com.example.InventoryManagementSystem.services;

import com.example.InventoryManagementSystem.dtos.CategoryDTO;
import com.example.InventoryManagementSystem.dtos.Response;

public interface CategoryService {

	Response createCategory(CategoryDTO categoryDTO);
	
	Response getAllCategories();
	
	Response getCategoryById(Long id);
	
	Response updateCategory(Long id, CategoryDTO categoryDTO);
	
	Response deleteCategory(Long id);
}
