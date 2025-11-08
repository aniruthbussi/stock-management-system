
package com.example.InventoryManagementSystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.InventoryManagementSystem.models.Supplier;



public interface SupplierRepository extends JpaRepository<Supplier, Long>{

	
}
