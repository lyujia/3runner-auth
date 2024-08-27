package com.nhnacademy.auth.dto.response;

import lombok.Builder;

@Builder
public record DormantResponse(String access, String refresh) {
}
