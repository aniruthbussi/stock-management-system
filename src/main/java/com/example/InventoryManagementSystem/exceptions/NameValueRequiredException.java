package com.example.InventoryManagementSystem.exceptions;

public class NameValueRequiredException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public NameValueRequiredException(String message) {
		super(message);
	}
}
