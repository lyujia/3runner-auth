package com.nhnacademy.auth.controller;

import com.nhnacademy.auth.adapter.MemberAdapter;
import com.nhnacademy.auth.dto.request.DormantAwakeRequest;
import com.nhnacademy.auth.dto.request.DormantRequest;
import com.nhnacademy.auth.dto.response.DormantResponse;
import com.nhnacademy.auth.entity.DormantObject;
import com.nhnacademy.auth.service.DormantService;
import com.nhnacademy.auth.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DormantController {
    private final DormantService dormantService;
    private final MemberAdapter memberAdapter;

    @PostMapping("/auth/dormant")
    public ApiResponse<DormantResponse> dormantCheck(@RequestBody DormantRequest request) {
        DormantObject resultObject = dormantService.checkVerificationCode(request.email(), request.code());
        if (resultObject != null) {
            DormantResponse response = DormantResponse.builder().access(resultObject.getAccess()).refresh(resultObject.getRefresh()).build();
            memberAdapter.dormantAwake(DormantAwakeRequest.builder().email(request.email()).build());
            return ApiResponse.success(response);

        } else {
            return ApiResponse.fail(400, null);
        }

    }

    @PostMapping("/auth/dormant/resend")
    ApiResponse<Void> resendDormant(@RequestBody String email) {
        try {
            dormantService.updateVerificationCode(email);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.fail(400, null);
        }
    }
}



