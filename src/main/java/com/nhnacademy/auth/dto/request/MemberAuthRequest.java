package com.nhnacademy.auth.dto.request;

import lombok.Builder;

/**
 * Bookstore server 에 멤버 정보를 얻기 위해 보내는 Request DTO
 */
@Builder
public record MemberAuthRequest(
	String email
) {
}
