package com.nhnacademy.auth.util;

import jakarta.servlet.http.Cookie;

/**
 * 쿠키 유틸리티 클래스
 *
 * @author 오연수
 */
public class CookieUtil {

	/**
	 * 쿠키를 생성한다.
	 *
	 * @param key 쿠키 이름
	 * @param value 쿠키 값
	 * @return 해당 쿠키
	 */
	public static Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24 * 60 * 60); // 24 시간
		cookie.setHttpOnly(true);
		cookie.setPath("/");

		return cookie;
	}
}
