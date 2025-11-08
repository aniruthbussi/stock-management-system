package com.example.InventoryManagementSystem.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.example.InventoryManagementSystem.enums.UserRole;
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
public class UserDTO {
		
	private Long id;
	
	private String name;
	
	private String email;
	
	private String password;
	
	private String phoneNumber;
	
	private UserRole role;
	
	private List<TransactionDTO> transactions;
	
	private LocalDateTime createdAt;

	
}
