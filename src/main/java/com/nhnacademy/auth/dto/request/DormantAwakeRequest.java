package com.nhnacademy.auth.dto.request;

import lombok.Builder;

@Builder
public record DormantAwakeRequest(String email) {
}
