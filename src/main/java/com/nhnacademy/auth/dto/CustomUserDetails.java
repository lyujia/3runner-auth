package com.nhnacademy.auth.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.nhnacademy.auth.dto.response.MemberAuthResponse;

/**
 * 커스텀 유저 디테일 클래스
 *
 * @author 오연수
 */
public class CustomUserDetails implements UserDetails {
	final String ROLE_PREFIX = "ROLE_";
	private final MemberAuthResponse memberAuthResponse;

	public CustomUserDetails(MemberAuthResponse memberAuthResponse) {
		this.memberAuthResponse = memberAuthResponse;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>();

		List<String> authorities = memberAuthResponse.auth();

		for (String authority : authorities) {
			collection.add(new SimpleGrantedAuthority(ROLE_PREFIX + authority));
		}
		return collection;
	}

	@Override
	public String getPassword() {
		return memberAuthResponse.password();
	}

	@Override
	public String getUsername() {
		return memberAuthResponse.email();
	}

	public Long getMemberId() {
		return memberAuthResponse.memberId();
	}
}
