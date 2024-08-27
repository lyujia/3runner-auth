package com.nhnacademy.auth.service.impl;

import com.nhnacademy.auth.adapter.MemberAdapter;
import com.nhnacademy.auth.dto.request.LastLoginRequest;
import com.nhnacademy.auth.util.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberAdapter memberAdapter;


    public MemberService(MemberAdapter memberAdapter) {
        this.memberAdapter = memberAdapter;
    }

    public ApiResponse<Void> setLastLogin(Long userId) {

        return memberAdapter.lastLoginUpdate(LastLoginRequest.builder().memberId(userId).build());

    }
}
