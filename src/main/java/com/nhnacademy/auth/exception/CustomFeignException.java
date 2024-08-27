package com.nhnacademy.auth.exception;

public class CustomFeignException extends RuntimeException {
	public CustomFeignException(String message) {
		super(message);
	}
}
