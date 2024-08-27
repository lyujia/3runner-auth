package com.nhnacademy.auth.filter;

import java.io.IOException;

import org.springframework.web.filter.GenericFilterBean;

import com.nhnacademy.auth.service.TokenService;
import com.nhnacademy.auth.util.JWTUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 커스텀한 로그아웃 필터
 * /auth/logout 경로로 들어오면 로그아웃 진행
 *
 * @author 오연수
 */
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {
	private final JWTUtil jwtUtil;
	private final TokenService tokenService;

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
		IOException,
		ServletException {
		doFilter((HttpServletRequest)servletRequest, (HttpServletResponse)servletResponse, filterChain);
	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		ServletException,
		IOException {
		//path and method verify
		String requestUri = request.getRequestURI();
		if (!requestUri.matches("^\\/auth/logout$")) {

			filterChain.doFilter(request, response);
			return;
		}
		String requestMethod = request.getMethod();
		if (!requestMethod.equals("POST")) {

			filterChain.doFilter(request, response);
			return;
		}

		String refresh = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("Refresh")) {
				refresh = cookie.getValue();
			}
		}

		//refresh null check
		if (refresh == null) {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		//expired check
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {

			//response status code
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtUtil.getCategory(refresh);
		if (!category.equals("REFRESH")) {

			//response status code
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		//redis 에 있는지 확인
		String uuid = jwtUtil.getUuid(refresh);
		Boolean isExist = tokenService.existsRefreshToken(uuid, refresh);
		if (!isExist) {

			//response status code
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		//로그아웃 진행
		//Refresh 토큰 redis 에서 제거
		tokenService.deleteTokenDetail(uuid);
		tokenService.deleteRefreshToken(uuid);

		response.setStatus(HttpServletResponse.SC_OK);
	}
}
