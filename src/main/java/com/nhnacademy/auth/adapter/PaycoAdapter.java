package com.nhnacademy.auth.adapter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nhnacademy.auth.dto.response.MemberAuthResponse;
import com.nhnacademy.auth.entity.UserProfile;

import jakarta.validation.Valid;

@FeignClient(url = "${feign.client.url}/bookstore",name = "PaycoAdapter")
public interface PaycoAdapter {
	@PostMapping("/members/oauth")
	MemberAuthResponse oauthMember(@RequestBody @Valid UserProfile userProfile);
}


