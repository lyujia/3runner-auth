package com.nhnacademy.auth.dto.request;

import lombok.Builder;

@Builder
public record DormantRequest (String email, String code){
}
