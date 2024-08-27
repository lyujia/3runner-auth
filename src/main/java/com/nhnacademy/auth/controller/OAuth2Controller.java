package com.nhnacademy.auth.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.auth.adapter.PaycoAdapter;
import com.nhnacademy.auth.dto.CustomUserDetails;
import com.nhnacademy.auth.dto.response.LoginResponse;
import com.nhnacademy.auth.dto.response.MemberAuthResponse;

import com.nhnacademy.auth.entity.UserProfile;
import com.nhnacademy.auth.service.impl.OAuth2AuthenticationService;
import com.nhnacademy.auth.service.TokenService;
import com.nhnacademy.auth.service.impl.UserProfileService;
import com.nhnacademy.auth.util.ApiResponse;
import com.nhnacademy.auth.util.CookieUtil;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class OAuth2Controller {
	private final OAuth2AuthenticationService oAuth2AuthenticationService;
	private final UserProfileService userProfileService;
	private final PaycoAdapter paycoAdapter;
	private final TokenService tokenService;
	private final ObjectMapper objectMapper;
	private final AuthenticationManager authenticationManager;
	public OAuth2Controller(OAuth2AuthenticationService oAuth2AuthenticationService, UserProfileService userProfileService,PaycoAdapter paycoAdapter,TokenService tokenService,ObjectMapper objectMapper,AuthenticationManager authenticationManager) {
		this.oAuth2AuthenticationService = oAuth2AuthenticationService;
		this.userProfileService = userProfileService;
		this.paycoAdapter = paycoAdapter;
		this.tokenService = tokenService;
		this.objectMapper = objectMapper;
		this.authenticationManager = authenticationManager;
	}
	@PostMapping("/auth/oauth2/callback")
	public ApiResponse<LoginResponse> handleOAuth2Redirect(@RequestBody String code) throws Exception {

		JsonNode jsonNode = oAuth2AuthenticationService.getToken(code).block();
		String client_id = "3RDUR8qJyORVrsI2PdkInS1";
		String access_token = Objects.requireNonNull(jsonNode).get("access_token").asText();

		JsonNode returnData = oAuth2AuthenticationService.getUserDate(client_id,access_token).block();
		System.out.println(returnData);
		UserProfile userProfile = new UserProfile(returnData);
		MemberAuthResponse response;
		try {
			response = paycoAdapter.oauthMember(userProfile);
		}catch (RuntimeException e) {
			return  ApiResponse.fail(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				new ApiResponse.Body<>(new LoginResponse("일반 회원 이메일인데 페이코 접속")));
		}
		CustomUserDetails customUserDetails = new CustomUserDetails(response);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(customUserDetails,null,customUserDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);


		List<String> tokens = tokenService.generateToken(response.email(), response.auth(), response.memberId());
		String access = tokens.get(0);
		String refresh = tokens.get(1);


		HttpServletResponse servletResponse = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

		if (servletResponse != null) {
			servletResponse.addHeader("Authorization", "Bearer " + access);
			servletResponse.addCookie(CookieUtil.createCookie("Refresh", refresh));
			servletResponse.setStatus(HttpStatus.OK.value());

			ApiResponse<LoginResponse> apiResponse = ApiResponse.success(new LoginResponse("인증 성공"));
			servletResponse.setStatus(HttpServletResponse.SC_OK);
			servletResponse.setContentType("application/json;charset=UTF-8");

			// 헤더와 쿠키가 추가되었는지 확인
			System.out.println(servletResponse);

			return apiResponse;
		}
		return  ApiResponse.fail(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
			new ApiResponse.Body<>(new LoginResponse("인증 실패")));
	}
}
