package com.nhnacademy.auth.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.auth.dto.TokenDetails;
import com.nhnacademy.auth.dto.request.RefreshRequest;
import com.nhnacademy.auth.dto.response.RefreshResponse;
import com.nhnacademy.auth.service.TokenService;
import com.nhnacademy.auth.util.ApiResponse;
import com.nhnacademy.auth.util.CookieUtil;
import com.nhnacademy.auth.util.JWTUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 토큰 컨트롤러
 *
 * @author 오연수
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class TokenController {
	private final JWTUtil jwtUtil;
	private final TokenService tokenService;

	/**
	 * 토큰을 재발급한다.
	 *
	 *
	 * @param refreshRequest 리프레시 토큰
	 * @param response HttpServletResponse
	 * @return 액세스 토큰
	 */
	@PostMapping("/reissue")
	public ApiResponse<RefreshResponse> reissue(@RequestBody RefreshRequest refreshRequest,
		HttpServletResponse response) {

		String refresh = refreshRequest.refreshToken();

		if (refresh == null) {
			log.error("Refresh token is null");
			return ApiResponse.badRequestFail(new ApiResponse.Body<>(new RefreshResponse("refresh token null", null)));
		}

		//expired check
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			return ApiResponse.badRequestFail(
				new ApiResponse.Body<>(new RefreshResponse("리프레시 토큰 만료", null)));
		}

		// 토큰이 refresh 인지 확인 (발급시 페이로드에 명시)
		String category = jwtUtil.getCategory(refresh);

		if (!category.equals("REFRESH")) {
			return ApiResponse.badRequestFail(
				new ApiResponse.Body<>(new RefreshResponse("유효하지 않은 리프레시 토큰", null)));
		}

		String uuid = jwtUtil.getUuid(refresh);

		// Redis 에 저장되어 있는지 확인 (refresh 토큰 내용도 비교)
		if (!tokenService.existsRefreshToken(uuid, refresh)) {

			return ApiResponse.badRequestFail(
				new ApiResponse.Body<>(new RefreshResponse("유효하지 않은 리프레시 토큰", null)));
		}

		TokenDetails tokenDetails = null;

		tokenDetails = tokenService.getTokenDetails(uuid);

		// 기존 uuid 삭제
		tokenService.deleteRefreshToken(uuid);
		tokenService.deleteTokenDetail(uuid);
		log.error("기존 uuid redis 삭제: {} ", uuid);

		// make new Access token
		List<String> tokens = tokenService.generateToken(tokenDetails.getEmail(), tokenDetails.getAuths(),
			tokenDetails.getMemberId());

		// jwt 생성후 헤더와 쿠키에 붙여준다.
		response.addHeader("Authorization", "Bearer " + tokens.getFirst());
		response.addCookie(CookieUtil.createCookie("Refresh", tokens.getLast()));
		response.setStatus(HttpStatus.OK.value());
		log.error("Access token: {}", tokens.getFirst());
		log.error("Refresh token: {}", tokens.getLast());
		return ApiResponse.success(new RefreshResponse("토큰 재발급 완료", tokens.getFirst()));
	}
}
