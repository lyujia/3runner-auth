package com.nhnacademy.auth.dto.request;

import lombok.Builder;

/**
 * Front server 에서 토큰 재발급 위해 받아오는 DTO
 *
 * @author 오연수
 */
@Builder
public record RefreshRequest(
	String refreshToken
) {
}
