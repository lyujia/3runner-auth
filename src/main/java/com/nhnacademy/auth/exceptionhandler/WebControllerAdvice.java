package com.nhnacademy.auth.exceptionhandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nhnacademy.auth.util.ApiResponse;

@RestControllerAdvice
public class WebControllerAdvice {

	@ExceptionHandler(AuthenticationException.class)
	public ApiResponse<ErrorResponseForm> runtimeExceptionHandler(RuntimeException e) {
		return ApiResponse.fail(HttpStatus.UNAUTHORIZED.value(),
			new ApiResponse.Body<>(
				ErrorResponseForm.builder()
					.title(e.getMessage())
					.status(HttpStatus.UNAUTHORIZED.value())
					.timestamp(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toString())
					.build()
			));
	}

}
