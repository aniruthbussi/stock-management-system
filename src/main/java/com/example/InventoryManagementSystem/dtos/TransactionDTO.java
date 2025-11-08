package com.example.InventoryManagementSystem.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.InventoryManagementSystem.enums.TransactionStatus;
import com.example.InventoryManagementSystem.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDTO {
		
	private Long id;
	
	private Integer totalProducts;
	
	private BigDecimal totalPrice;
	
	private TransactionType transactionType; // purchase, sale, return
	
	private TransactionStatus status; // pending, completed, processing
	
	private String description;
	
	private String note;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updateAt;
	
	private ProductDTO product;
	
	private UserDTO user;
	
	private SupplierDTO supplier;
	
	
	
}
