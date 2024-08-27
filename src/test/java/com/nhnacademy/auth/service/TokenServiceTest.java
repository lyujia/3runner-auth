package com.nhnacademy.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.auth.dto.TokenDetails;
import com.nhnacademy.auth.service.impl.TokenServiceImpl;
import com.nhnacademy.auth.util.JWTUtil;

class TokenServiceTest {

	@Mock
	private JWTUtil jwtUtil;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private HashOperations<String, String, TokenDetails> hashOperations;

	@InjectMocks
	private TokenServiceImpl tokenService;

	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		doReturn(hashOperations).when(redisTemplate).opsForHash();
	}

	// @Test
	// void generateToken() throws JsonProcessingException {
	// 	String email = "testUser@test.com";
	// 	List<String> auths = Arrays.asList("ROLE_USER");
	// 	Long memberId = 1L;
	//
	// 	String uuid = UUID.randomUUID().toString();
	// 	String accessToken = "accessToken";
	// 	String refreshToken = "refreshToken";
	//
	// 	when(jwtUtil.generateTokenWithUuid(eq("ACCESS"), anyString(), eq(3600000L))).thenReturn(accessToken);
	// 	when(jwtUtil.generateTokenWithUuid(eq("REFRESH"), anyString(), eq(86400000L))).thenReturn(refreshToken);
	//
	// 	List<String> tokens = tokenService.generateToken(email, auths, memberId);
	//
	// 	assertNotNull(tokens);
	// 	assertEquals(2, tokens.size());
	// 	assertEquals(accessToken, tokens.get(0));
	// 	assertEquals(refreshToken, tokens.get(1));
	// }

	@Test
	void getTokenDetails() throws JsonProcessingException {
		String uuid = UUID.randomUUID().toString();
		TokenDetails tokenDetails = new TokenDetails("testUser", Arrays.asList("ROLE_USER"), 1L);

		when(redisTemplate.opsForHash().get("token_details", uuid)).thenReturn(
			objectMapper.writeValueAsString(tokenDetails));

		TokenDetails result = tokenService.getTokenDetails(uuid);

		assertNotNull(result);
		assertEquals(tokenDetails.getEmail(), result.getEmail());
		assertEquals(tokenDetails.getMemberId(), result.getMemberId());
	}

	@Test
	void deleteTokenDetail() {
		String uuid = UUID.randomUUID().toString();

		tokenService.deleteTokenDetail(uuid);

		verify(redisTemplate.opsForHash(), times(1)).delete("token_details", uuid);
	}
}

