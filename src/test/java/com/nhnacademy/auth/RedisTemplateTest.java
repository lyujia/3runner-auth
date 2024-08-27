package com.nhnacademy.auth;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.auth.dto.TokenDetails;

@SpringBootTest
@TestPropertySource(properties = "feign.client.url=http://localhost:8080")
public class RedisTemplateTest {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private static final String TOKEN_DETAILS = "token_details_test";

	private ObjectMapper objectMapper = new ObjectMapper();

	@AfterEach
	public void cleanUp() {
		redisTemplate.delete(TOKEN_DETAILS);
	}

	@Test
	public void testRedisPutAndGet() throws JsonProcessingException {
		// Given
		String uuid = "test-uuid";
		TokenDetails tokenDetails = new TokenDetails("test@testtest.com", Arrays.asList("ROLE_USER"), 77777L);

		// When
		redisTemplate.opsForHash().put(TOKEN_DETAILS, uuid, objectMapper.writeValueAsString(tokenDetails));
		String data = (String)redisTemplate.opsForHash().get(TOKEN_DETAILS, uuid);
		TokenDetails retrievedTokenDetails = objectMapper.readValue(data, TokenDetails.class);
		// Then
		assertThat(retrievedTokenDetails).isNotNull();
		assertThat(retrievedTokenDetails.getEmail()).isEqualTo("test@testtest.com");
		assertThat(retrievedTokenDetails.getMemberId()).isEqualTo(77777L);
	}

	@Test
	public void testRedisDelete() throws JsonProcessingException {
		// Given
		String uuid = "test-uuid";
		TokenDetails tokenDetails = new TokenDetails("test@testtest.com", Arrays.asList("ROLE_USER"), 77777L);

		// When
		redisTemplate.opsForHash().put(TOKEN_DETAILS, uuid, objectMapper.writeValueAsString(tokenDetails));
		String data = (String)redisTemplate.opsForHash().get(TOKEN_DETAILS, uuid);
		TokenDetails retrievedTokenDetails = objectMapper.readValue(data, TokenDetails.class);

		// Then
		assertThat(retrievedTokenDetails).isNotNull();
		assertThat(retrievedTokenDetails.getEmail()).isEqualTo("test@testtest.com");
		assertThat(retrievedTokenDetails.getMemberId()).isEqualTo(77777L);

		redisTemplate.opsForHash().delete(TOKEN_DETAILS, "test-uuid");

		data = (String)redisTemplate.opsForHash().get(TOKEN_DETAILS, "test-uuid");

		assertThat(data).isNull();
	}

	@Test
	public void testRedisExpire() throws InterruptedException, JsonProcessingException {
		// Given
		String uuid = "test-uuid";
		TokenDetails tokenDetails = new TokenDetails("test@testtest.com", Arrays.asList("ROLE_USER"), 77777L);

		// When
		redisTemplate.opsForHash().put(TOKEN_DETAILS, uuid, objectMapper.writeValueAsString(tokenDetails));

		// Set expire time to 2 seconds
		redisTemplate.expire(TOKEN_DETAILS, 2, TimeUnit.SECONDS);

		// Wait for 3 seconds to ensure the key expires
		Thread.sleep(3000);

		String data = (String)redisTemplate.opsForHash().get(TOKEN_DETAILS, uuid);

		// Then
		assertThat(data).isNull();
	}
}

