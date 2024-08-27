package com.nhnacademy.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.auth.service.impl.MemberService;
import com.nhnacademy.auth.adapter.DoorayAdapter;
import com.nhnacademy.auth.filter.CustomAuthenticationFilter;
import com.nhnacademy.auth.filter.CustomLogoutFilter;
import com.nhnacademy.auth.service.DormantService;
import com.nhnacademy.auth.service.TokenService;
import com.nhnacademy.auth.util.JWTUtil;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final AuthenticationConfiguration authenticationConfiguration;
	private final ObjectMapper objectMapper;
	private final TokenService tokenService;
	private final JWTUtil jwtUtil;
	private final MemberService memberService;
	private final DormantService dormantService;
	private final DoorayAdapter doorayAdapter;

	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,
		ObjectMapper objectMapper, TokenService tokenService, JWTUtil jwtUtil, MemberService memberService,DormantService dormantService,DoorayAdapter doorayAdapter) {
		this.authenticationConfiguration = authenticationConfiguration;
		this.objectMapper = objectMapper;
		this.tokenService = tokenService;
		this.jwtUtil = jwtUtil;
		this.memberService = memberService;
		this.dormantService = dormantService;
		this.doorayAdapter = doorayAdapter;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable);
		http
			.formLogin(AbstractHttpConfigurer::disable);
		http
			.httpBasic(AbstractHttpConfigurer::disable);

		http.authorizeHttpRequests(
			authorize -> authorize.anyRequest().permitAll()
		);

		http
			.addFilterAt(new CustomAuthenticationFilter(authenticationManager(authenticationConfiguration),
				objectMapper, tokenService, memberService,dormantService,doorayAdapter), UsernamePasswordAuthenticationFilter.class);

		http
			.addFilterBefore(new CustomLogoutFilter(jwtUtil, tokenService), LogoutFilter.class);

		http
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
}
