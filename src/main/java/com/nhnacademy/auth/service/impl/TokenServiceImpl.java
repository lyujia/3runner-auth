package com.nhnacademy.auth.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.auth.dto.TokenDetails;
import com.nhnacademy.auth.service.TokenService;
import com.nhnacademy.auth.util.JWTUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 토큰 서비스 구현체
 *
 * @author 오연수
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
	private final String TOKEN_DETAILS = "token_details";
	private final String REFRESH_TOKEN = "refresh_token";
	private final Long ACCESS_TOKEN_TTL = 3L;
	private final Long REFRESH_TOKEN_TTL = 604800000L; // 7 * 24 * 60 * 60 * 1000

	private final JWTUtil jwtUtil;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public List<String> generateToken(String username, List<String> auths, Long memberId) {
		String uuid = UUID.randomUUID().toString();
		log.error("새로운 uuid: {}", uuid);

		TokenDetails tokenDetails = new TokenDetails(username, auths, memberId);
		try {
			redisTemplate.opsForHash().put(TOKEN_DETAILS, uuid, objectMapper.writeValueAsString(tokenDetails));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		redisTemplate.expire(TOKEN_DETAILS, ACCESS_TOKEN_TTL, TimeUnit.HOURS);

		String accessToken = jwtUtil.generateTokenWithUuid("ACCESS", uuid, ACCESS_TOKEN_TTL);
		String refreshToken = jwtUtil.generateTokenWithUuid("REFRESH", uuid, REFRESH_TOKEN_TTL);
		redisTemplate.opsForHash().put(REFRESH_TOKEN, uuid, refreshToken);
		return Arrays.asList(accessToken, refreshToken);
	}

	@Override
	public TokenDetails getTokenDetails(String uuid) {
		String data = (String)redisTemplate.opsForHash().get(TOKEN_DETAILS, uuid);
		try {
			return objectMapper.readValue(data, TokenDetails.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteTokenDetail(String uuid) {
		redisTemplate.opsForHash().delete(TOKEN_DETAILS, uuid);
	}

	@Override
	public String getRefreshToken(String uuid) {
		return (String)redisTemplate.opsForHash().get(REFRESH_TOKEN, uuid);
	}

	@Override
	public void deleteRefreshToken(String uuid) {
		redisTemplate.opsForHash().delete(REFRESH_TOKEN, uuid);
	}

	@Override
	public boolean existsRefreshToken(String uuid, String inputRefresh) {
		String storedRefresh = getRefreshToken(uuid);
		return Objects.nonNull(storedRefresh) && inputRefresh.equals(storedRefresh);
	}

}
