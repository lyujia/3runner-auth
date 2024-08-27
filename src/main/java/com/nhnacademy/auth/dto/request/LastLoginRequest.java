package com.nhnacademy.auth.dto.request;

import lombok.Builder;

@Builder
public record LastLoginRequest(Long memberId) {
}
