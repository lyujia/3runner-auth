package com.nhnacademy.auth.service;

import java.util.List;

import com.nhnacademy.auth.dto.TokenDetails;

/**
 * 토큰 서비스 인터페이스
 *
 * @author 오연수
 */
public interface TokenService {
	/**
	 * 액세스 토큰, 리프레시 토큰을 새로 만들어 리스트를 반환한다.
	 *
	 * @param username 이메일
	 * @param auths 권한 스트링 리스트
	 * @param memberId 멤버 아이디
	 * @return 토큰 값 담긴 리스트
	 */
	List<String> generateToken(String username, List<String> auths, Long memberId);

	/**
	 * Redis 에서 uuid 로 TokenDetails 객체를 가져온다.
	 *
	 * @param uuid the uuid
	 * @return the token details
	 */
	TokenDetails getTokenDetails(String uuid);

	/**
	 * Redis 에서 uuid 로 저장된 객체 삭제한다.
	 *
	 * @param uuid the uuid
	 */
	void deleteTokenDetail(String uuid);

	/**
	 * Redis 에서 uuid 로 RefreshToken 을 가져온다.
	 *
	 * @param uuid the uuid
	 * @return the refresh token
	 */
	String getRefreshToken(String uuid);

	/**
	 * Redis 에서 uuid 로 RefreshToken 을 삭제한다.
	 *
	 * @param uuid the uuid
	 */
	void deleteRefreshToken(String uuid);

	/**
	 * Redis 에서 uuid, refresh token 가 저장되어 있는지 확인한다.
	 *
	 * @param uuid the uuid
	 * @param inputRefresh refresh token
	 * @return 저장 여부
	 */
	boolean existsRefreshToken(String uuid, String inputRefresh);

}
