package com.nhnacademy.auth.adapter;

import com.nhnacademy.auth.dto.request.DormantAwakeRequest;
import com.nhnacademy.auth.dto.request.LastLoginRequest;
import com.nhnacademy.auth.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * OpenFeign 사용한 로그인 어댑터
 */
@FeignClient(url = "${feign.client.url}/bookstore", name = "MemberAdapter")
public interface MemberAdapter {

    @PutMapping("/members/lastLogin")
    ApiResponse<Void> lastLoginUpdate(@RequestBody LastLoginRequest lastLoginRequest);

    @PutMapping("/members/lastLogin/dormantAwake")
    ApiResponse<Void> dormantAwake(@RequestBody DormantAwakeRequest dormantAwakeRequest);
}
