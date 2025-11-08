package com.example.InventoryManagementSystem.services.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.InventoryManagementSystem.dtos.ProductDTO;
import com.example.InventoryManagementSystem.dtos.Response;
import com.example.InventoryManagementSystem.exceptions.NotFoundException;
import com.example.InventoryManagementSystem.models.Category;
import com.example.InventoryManagementSystem.models.Product;
import com.example.InventoryManagementSystem.repositories.CategoryRepository;
import com.example.InventoryManagementSystem.repositories.ProductRepository;
import com.example.InventoryManagementSystem.services.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final ModelMapper modelmapper;
	private final CategoryRepository categoryRepository;
	private Object product;
	
	private static final String IMAGE_DIRECTORY = System.getProperty("user.dir") + "/product-images/";
	
	//After your Frontend is Setup Change the Image Directory to the Frontend you are Using
	
	private static final String IMAGE_DIRECTORY_2 = "D:/AniJaga/FullStackDevelopmentProjects_BESANT/StockManagementSystem-frontend/stock-react/public/products/";
	
	@Override
	public Response saveProduct(ProductDTO productDTO, MultipartFile imageFile) {
	    if (productRepository.existsBySku(productDTO.getSku())) {
	        return Response.builder()
	                .status(409)
	                .message("SKU already exists. Please use a unique SKU.")
	                .build();
	    }

	    Category category = categoryRepository.findById(productDTO.getCategoryId())
	            .orElseThrow(() -> new NotFoundException("Category Not Found"));

	    Product productToSave = Product.builder()
	            .name(productDTO.getName())
	            .sku(productDTO.getSku())
	            .price(productDTO.getPrice())
	            .stockQuantity(productDTO.getStockQuantity())
	            .description(productDTO.getDescription())
	            .category(category)
	            .build();

	    if (imageFile != null && !imageFile.isEmpty()) {
	        log.info("Image file exists");

	        //String imagePath = saveImage(imageFile);//use this when you haven't setup your frontend
	        String imagePath = saveImage2(imageFile);//use this when you are set up your frontend locally but haven't deployed to production
	        productToSave.setImageUrl(imagePath);
	    }

	    productRepository.save(productToSave);

	    return Response.builder()
	            .status(200)
	            .message("Product successfully saved")
	            .build();
	}

	@Override
	public Response updateProduct(ProductDTO productDTO, MultipartFile imageFile) {
		
		Product existingProduct = productRepository.findById(productDTO.getProductId())
				.orElseThrow(()-> new NotFoundException("Product Not Found"));
		
		if(imageFile != null && !imageFile.isEmpty()) {
	        //String imagePath = saveImage(imageFile);//use this when you haven't setup your frontend
	        String imagePath = saveImage2(imageFile);//use this when you are set up your frontend locally but haven't deployed to production
			existingProduct.setImageUrl(imagePath);
		}
		
		if(productDTO.getCategoryId() != null && productDTO.getCategoryId() > 0) {
			Category category = categoryRepository.findById(productDTO.getCategoryId())
					.orElseThrow(()-> new NotFoundException("Category Not Found"));
			existingProduct.setCategory(category);
		}
		
		if(productDTO.getName() !=null && !productDTO.getName().isBlank()) {
			existingProduct.setName(productDTO.getName());
		}
		
		if (productDTO.getSku() != null && !productDTO.getSku().isBlank()) {
		    if (!productDTO.getSku().equals(existingProduct.getSku()) &&
		        productRepository.existsBySku(productDTO.getSku())) {
		        return Response.builder()
		                .status(409)
		                .message("SKU already exists. Please choose a different SKU.")
		                .build();
		    }
		    existingProduct.setSku(productDTO.getSku());
		}

		
		if(productDTO.getDescription() !=null && !productDTO.getDescription().isBlank()) {
			existingProduct.setDescription(productDTO.getDescription());
		}
		
		if(productDTO.getPrice() !=null && !(productDTO.getPrice().compareTo(BigDecimal.ZERO) >= 0)) {
			existingProduct.setPrice(productDTO.getPrice());
		}
		
		if(productDTO.getStockQuantity() !=null && !(productDTO.getStockQuantity() >= 0)) {
			existingProduct.setStockQuantity(productDTO.getStockQuantity());
		}
		
		productRepository.save(existingProduct);
		
		return Response.builder()
				.status(200)
				.message("Product Updated successfully")
				.build();
		
	}
	@Override
	public Response getAllProducts() {
		
		List<Product> productList = productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
		
		List<ProductDTO> productDTOList = modelmapper.map(productList, new TypeToken<List<ProductDTO>>()
		{}.getType());
				
		return Response.builder()
				.status(200)
				.message("success")
				.products(productDTOList)
				.build();
	}
	@Override
	public Response getProductById(Long id) {
		Product existingProduct = productRepository.findById(id)
				.orElseThrow(()->new NotFoundException("Product Not Found"));
		
		return Response.builder()
				.status(200)
				.message("success")
				.product(modelmapper.map(existingProduct, ProductDTO.class))
				.build();
	}
	@Override
	public Response deleteProduct(Long id) {
		productRepository.findById(id)
			.orElseThrow(()->new NotFoundException("Product Not Found"));

	productRepository.deleteById(id);
	
	return Response.builder()
			.status(200)
			.message("Product Deleted successfully")
			.build();
	}
	@Override
	public Response searchProduct(String input) {
		
		List<Product> products = productRepository.findByNameContainingOrDescriptionContaining(input, input);
		
		if(products.isEmpty()) {
			throw new NotFoundException("Product Not Found");
		}
		
		List<ProductDTO> productDTOList = modelmapper.map(products, new TypeToken<List<ProductDTO>>()
				{}.getType());
		
		return Response.builder()
				.status(200)
				.message("success")
				.products(productDTOList)
				.build();
		
	}
	
	//This save to the root of your PROJECT
	
	private String saveImage(MultipartFile imageFile) {
		String contentType = imageFile.getContentType();

	    if (contentType == null || !contentType.startsWith("image/") || imageFile.getSize() > 1024L * 1024 * 1024) {
	        throw new IllegalArgumentException("Only image files under 1GB are allowed");
	    }
		
		File directory = new File(IMAGE_DIRECTORY);
		
		if(!directory.exists()) {
			directory.mkdir();
			log.info("Directory was created");
		}
		
		String uniqueFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
		
		String imagePath = IMAGE_DIRECTORY + uniqueFileName;
		
		try {
			File destinationFile = new File(imagePath);
			imageFile.transferTo(destinationFile);
		}catch (Exception e) {
			throw new IllegalArgumentException("Error saving Image: " + e.getMessage());
		}
		return imagePath;
		
	}
	
	//This Saved Image to the Public Folder in your FRONTEND
	//Use this if your have setup your FRONTEND
	
	private String saveImage2(MultipartFile imageFile) {
		String contentType = imageFile.getContentType();

	    if (contentType == null || !contentType.startsWith("image/") || imageFile.getSize() > 1024L * 1024 * 1024) {
	        throw new IllegalArgumentException("Only image files under 1GB are allowed");
	    }
		
		File directory = new File(IMAGE_DIRECTORY_2);
		
		if(!directory.exists()) {
			directory.mkdir();
			log.info("Directory was created");
		}
		
		String uniqueFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
		
		String imagePath = IMAGE_DIRECTORY_2 + uniqueFileName;
		
		try {
			File destinationFile = new File(imagePath);
			imageFile.transferTo(destinationFile);
		}catch (Exception e) {
			throw new IllegalArgumentException("Error saving Image: " + e.getMessage());
		}
		return "products/"+uniqueFileName;
		
	}
	
}
