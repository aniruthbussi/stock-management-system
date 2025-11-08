package com.example.InventoryManagementSystem.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.InventoryManagementSystem.dtos.CategoryDTO;
import com.example.InventoryManagementSystem.dtos.Response;
import com.example.InventoryManagementSystem.services.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;
	
	@PostMapping("/add")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Response> createCategory(@RequestBody @Valid CategoryDTO categoryDTO){
		return ResponseEntity.ok(categoryService.createCategory(categoryDTO));
	}
	@GetMapping("/all")
	public ResponseEntity<Response> getAllCategories() {
	    return ResponseEntity.ok(categoryService.getAllCategories());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Response> getCategoryById(@PathVariable Long id){
		return ResponseEntity.ok(categoryService.getCategoryById(id));
	}
	@PutMapping("/update/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Response> updateUser(@PathVariable Long id, @RequestBody @Valid CategoryDTO categoryDTO){
		return ResponseEntity.ok(categoryService.updateCategory(id, categoryDTO));
	}
	
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Response> deleteCategory(@PathVariable Long id){
		return ResponseEntity.ok(categoryService.deleteCategory(id));
	}
	
	
}
