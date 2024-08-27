package com.nhnacademy.auth.dto.response;

import lombok.Builder;

/**
 * Front server 에 토큰 재발급 후 보내는 DTO
 *
 * @author 오연수
 */
@Builder
public record RefreshResponse(
	String message,
	String accessToken
) {
}
