package com.nhnacademy.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.nhnacademy.auth.service.impl.CustomUserDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.nhnacademy.auth.adapter.LoginAdapter;
import com.nhnacademy.auth.dto.CustomUserDetails;
import com.nhnacademy.auth.dto.request.MemberAuthRequest;
import com.nhnacademy.auth.dto.response.MemberAuthResponse;
import com.nhnacademy.auth.util.ApiResponse;

public class CustomUserDetailServiceTest {
	@Mock
	private LoginAdapter loginAdapter;

	@InjectMocks
	private CustomUserDetailService customUserDetailService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testLoadUserByUsername() throws Exception {
		String email = "test@example.com";
		MemberAuthResponse response = new MemberAuthResponse(email, "password", List.of("USER"), 1L);

		when(loginAdapter.memberLogin(any(MemberAuthRequest.class))).thenReturn(ApiResponse.success(response));

		CustomUserDetails userDetails = (CustomUserDetails)customUserDetailService.loadUserByUsername(email);

		assertEquals(email, userDetails.getUsername());
		assertEquals("password", userDetails.getPassword());
		assertEquals("ROLE_USER", userDetails.getAuthorities().iterator().next().getAuthority());

		verify(loginAdapter, times(1)).memberLogin(any(MemberAuthRequest.class));
	}

	@Test
	void testLoadUserByUsernameThrowsException() {
		String email = "test@example.com";

		when(loginAdapter.memberLogin(any(MemberAuthRequest.class))).thenThrow(new RuntimeException());

		assertThrows(UsernameNotFoundException.class, () -> customUserDetailService.loadUserByUsername(email));

		verify(loginAdapter, times(1)).memberLogin(any(MemberAuthRequest.class));
	}
}
