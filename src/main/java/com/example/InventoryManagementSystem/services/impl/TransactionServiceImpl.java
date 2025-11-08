package com.example.InventoryManagementSystem.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.InventoryManagementSystem.dtos.Response;
import com.example.InventoryManagementSystem.dtos.TransactionDTO;
import com.example.InventoryManagementSystem.dtos.TransactionRequest;
import com.example.InventoryManagementSystem.enums.TransactionStatus;
import com.example.InventoryManagementSystem.enums.TransactionType;
import com.example.InventoryManagementSystem.exceptions.NameValueRequiredException;
import com.example.InventoryManagementSystem.exceptions.NotFoundException;
import com.example.InventoryManagementSystem.models.Product;
import com.example.InventoryManagementSystem.models.Supplier;
import com.example.InventoryManagementSystem.models.Transaction;
import com.example.InventoryManagementSystem.models.User;
import com.example.InventoryManagementSystem.repositories.ProductRepository;
import com.example.InventoryManagementSystem.repositories.SupplierRepository;
import com.example.InventoryManagementSystem.repositories.TransactionRepository;
import com.example.InventoryManagementSystem.services.TransactionService;
import com.example.InventoryManagementSystem.services.UserService;
import com.example.InventoryManagementSystem.specification.TransactionFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

	private final TransactionRepository transactionRepository;
	private final ProductRepository productRepository;
	private final SupplierRepository supplierRepository;
	private final UserService userService;
	private final ModelMapper modelMapper;
	@Override
	public Response purchase(TransactionRequest transactionRequest) {
		
		Long productId = transactionRequest.getProductId();
		Long supplierId = transactionRequest.getSupplierId();
		Integer quantity = transactionRequest.getQuantity();
		
		if(supplierId == null) throw new NameValueRequiredException("Supplier Id is Required");
		
		Product product = productRepository.findById(productId)
				.orElseThrow(()-> new NotFoundException("Product Not Found"));
		
		Supplier supplier = supplierRepository.findById(supplierId)
				.orElseThrow(()-> new NotFoundException("Supplier Not Found"));
		
		User user = userService.getCurrentLoggedInUser();
		
		product.setStockQuantity(product.getStockQuantity() + quantity);
		productRepository.save(product);
		
		Transaction transaction = Transaction.builder()
				.transactionType(TransactionType.PURCHASE)
				.status(TransactionStatus.COMPLETED)
				.product(product)
				.user(user)
				.supplier(supplier)
				.totalProducts(quantity)
				.totalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
				.note(transactionRequest.getNote())
				.build();
		transactionRepository.save(transaction);
		return Response.builder()
				.status(200)
				.message("Purchase Made successfully")
				.build();
	}
	@Override
	public Response sell(TransactionRequest transactionRequest) {
	    Long productId = transactionRequest.getProductId();
	    Integer quantity = transactionRequest.getQuantity();

	    Product product = productRepository.findById(productId)
	            .orElseThrow(() -> new NotFoundException("Product Not Found"));

	    // Fetch current user
	    User user = userService.getCurrentLoggedInUser();

	    // Validate stock
	    if (product.getStockQuantity() < quantity) {
	        throw new IllegalArgumentException("Insufficient stock to complete the sale.");
	    }

	    // Decrease stock
	    product.setStockQuantity(product.getStockQuantity() - quantity);
	    productRepository.save(product);

	    // Save transaction
	    Transaction transaction = Transaction.builder()
	            .transactionType(TransactionType.SALE)
	            .status(TransactionStatus.COMPLETED)
	            .product(product)
	            .user(user)
	            .totalProducts(quantity)
	            .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
	            .description(transactionRequest.getDescription())
	            .note(transactionRequest.getNote())
	            .build();

	    transactionRepository.save(transaction);

	    return Response.builder()
	            .status(200)
	            .message("Product Sale Successfully Made")
	            .build();
	}

	@Override
	public Response returnToSupplier(TransactionRequest transactionRequest) {
		
		Long productId = transactionRequest.getProductId();
		Long supplierId = transactionRequest.getSupplierId();
		Integer quantity = transactionRequest.getQuantity();
		
		if(supplierId == null) throw new NameValueRequiredException("Supplier Id is Required");
		
		Product product = productRepository.findById(productId)
				.orElseThrow(()-> new NotFoundException("Product Not Found"));
		
		Supplier supplier = supplierRepository.findById(supplierId)
				.orElseThrow(()-> new NotFoundException("Supplier Not Found"));
		
		User user = userService.getCurrentLoggedInUser();
		
		product.setStockQuantity(product.getStockQuantity() + quantity);
		productRepository.save(product);
		
		Transaction transaction = Transaction.builder()
				.transactionType(TransactionType.RETURN_TO_SUPPLIER)
				.status(TransactionStatus.PROCESSING)
				.product(product)
				.user(user)
				.totalProducts(quantity)
				.totalPrice(BigDecimal.ZERO)
				.description(transactionRequest.getDescription())
				.note(transactionRequest.getNote())
				.build();
		
		transactionRepository.save(transaction);
		
		return Response.builder()
				.status(200)
				.message("Product Returned in Progress")
				.build();
	}
	@Override
	public Response getAllTransactions(int page, int size, String filter) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		
		Specification<Transaction> spec = TransactionFilter.byFilter(filter);
		Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);
		
		List<TransactionDTO> transactionDTOS = modelMapper.map(transactionPage.getContent(), new TypeToken<List<TransactionDTO>>() 
		{}.getType());
		
		transactionDTOS.forEach(transactionDTO -> {
			transactionDTO.setUser(null);
			transactionDTO.setProduct(null);
			transactionDTO.setSupplier(null);
		});
		
		return Response.builder()
				.status(200)
				.message("success")
				.transactions(transactionDTOS)
				.totalElements(transactionPage.getTotalElements())
				.totalPages(transactionPage.getTotalPages())
				.build();
	}
	@Override
	public Response getAllTransactionById(Long id) {
		
		Transaction transaction = transactionRepository.findById(id)
				.orElseThrow(()-> new NotFoundException("Transaction Not Found"));
		
		TransactionDTO transactionDTO = modelMapper.map(transaction, TransactionDTO.class);
		
		transactionDTO.getUser().setTransactions(null);
		
		return Response.builder()
				.status(200)
				.message("success")
				.transaction(transactionDTO)
				.build();
	}
	@Override
	public Response getAllTransactionByMonthAndYear(int Month, int year) {
		List<Transaction> transactions = transactionRepository.findAll(TransactionFilter.byMonthAndYear(Month, year));
		
		List<TransactionDTO> transactionDTOS = modelMapper.map(transactions, new TypeToken<List<TransactionDTO>>()
				{}.getType());
		
		transactionDTOS.forEach(transactionDTO -> {
			transactionDTO.setUser(null);
			transactionDTO.setProduct(null);
			transactionDTO.setProduct(null);			
		});
		
		return Response.builder()
				.status(200)
				.message("success")
				.transactions(transactionDTOS)
				.build();
	}
	@Override
	public Response updateTransactionStatus(Long transactionId, TransactionStatus status) {
		
		Transaction existingTransaction = transactionRepository.findById(transactionId)
				.orElseThrow(()-> new NotFoundException("Transaction Not Found"));
		
		existingTransaction.setStatus(status);
		existingTransaction.setUpdateAt(LocalDateTime.now());
		
		transactionRepository.save(existingTransaction);
		
		return Response.builder()
				.status(200)
				.message("Transaction Status Successfully Updated")
				.build();
	}
	
	
}
