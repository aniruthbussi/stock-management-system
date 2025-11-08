package com.example.InventoryManagementSystem.services;

import com.example.InventoryManagementSystem.dtos.Response;
import com.example.InventoryManagementSystem.dtos.TransactionRequest;
import com.example.InventoryManagementSystem.enums.TransactionStatus;

public interface TransactionService {

	Response purchase(TransactionRequest transactionRequest);
	
	Response sell(TransactionRequest transactionRequest);
	
	Response returnToSupplier(TransactionRequest transactionRequest);
	
	Response getAllTransactions(int page, int size, String filter);
	
	Response getAllTransactionById(Long id);
	
	Response getAllTransactionByMonthAndYear(int Month, int year);
	
	Response updateTransactionStatus(Long transactionId, TransactionStatus status);
}
