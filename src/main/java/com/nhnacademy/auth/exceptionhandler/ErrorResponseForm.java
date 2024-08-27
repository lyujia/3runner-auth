package com.nhnacademy.auth.exceptionhandler;

import lombok.Builder;

@Builder
public record ErrorResponseForm(String title, int status, String timestamp) {
}