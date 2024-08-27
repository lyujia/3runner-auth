package com.nhnacademy.auth.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Redis 에 저장할 정보
 * 액세스 토큰의 Uuid 를 Key 로 불러올 수 있다.
 *
 * @author 오연수
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TokenDetails implements Serializable {
	private String email;
	private List<String> auths;
	private Long memberId;
}
