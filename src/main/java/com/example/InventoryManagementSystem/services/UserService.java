package com.example.InventoryManagementSystem.services;

import com.example.InventoryManagementSystem.dtos.*;
import com.example.InventoryManagementSystem.models.User;

public interface UserService {

    Response registerUser(RegisterRequest request);

    Response loginUser(LoginRequest loginRequest);

    Response getAllUsers();

    User getCurrentLoggedInUser();

    Response getUserById(Long id);

    Response updateUser(Long id, UserDTO userDTO);

    Response deleteUser(Long id);

    Response getUserTransactions(Long id);
}
