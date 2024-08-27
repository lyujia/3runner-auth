package com.nhnacademy.auth.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.auth.service.impl.MemberService;
import com.nhnacademy.auth.adapter.DoorayAdapter;
import com.nhnacademy.auth.dto.CustomUserDetails;
import com.nhnacademy.auth.dto.request.LoginRequest;
import com.nhnacademy.auth.dto.response.LoginResponse;
import com.nhnacademy.auth.service.DormantService;
import com.nhnacademy.auth.service.TokenService;
import com.nhnacademy.auth.util.ApiResponse;
import com.nhnacademy.auth.util.CookieUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 커스텀한 인증 필터
 * /auth/login 경로로 들어오면 동작한다.
 * email, password 로 로그인 동작하며,
 * 성공 시 JWT 생성 후 응답 헤더에 Authorization Header 와 Refresh cookie 가 추가된다.
 *
 * @author 오연수
 */
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final ObjectMapper objectMapper;
	private final TokenService tokenService;
	private final MemberService memberService;
	private final DormantService dormantService;
	private final DoorayAdapter doorayAdapter;

	public CustomAuthenticationFilter(AuthenticationManager authenticationManager,
		ObjectMapper objectMapper, TokenService tokenService, MemberService memberService,DormantService dormantService, DoorayAdapter doorayAdapter) {
		this.authenticationManager = authenticationManager;
		this.objectMapper = objectMapper;
		this.tokenService = tokenService;
		this.setFilterProcessesUrl("/auth/login");
		this.memberService = memberService;
		this.dormantService = dormantService;
		this.doorayAdapter = doorayAdapter;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {
		LoginRequest loginRequest = null;
		try {
			loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.email(),
			loginRequest.password());
		return authenticationManager.authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authResult) throws IOException, ServletException {
		CustomUserDetails customUserDetails = (CustomUserDetails)authResult.getPrincipal();

		String username = customUserDetails.getUsername();
		Long memberId = customUserDetails.getMemberId();

		Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
		List<String> auths = authorities.stream()
			.map(GrantedAuthority::getAuthority)
			.toList();

		List<String> tokens = tokenService.generateToken(username, auths, memberId);
		String access = tokens.get(0);
		String refresh = tokens.get(1);

		SecurityContextHolder.getContext().setAuthentication(authResult);

		ApiResponse<Void> result= memberService.setLastLogin(memberId);//만약에 휴먼일 경우 lastlogin이 변경되지 않고 그냥 결과가 false로 반환된다.
		//폼에...비밀정보를 받을 수있으려나...
		ApiResponse<LoginResponse> apiResponse;
		if(!result.getHeader().isSuccessful()){

			dormantService.saveVerificationCode(username,access,refresh);
			//dormant 에다가 값들 넣는다. 아이디랑, uuid랑, access값이랑 refresh값이랑 들어가게 된다.//휴먼 계정일 경우

			response.addHeader("Authorization","Bearer "+"WakeDormantAccount");
			response.addCookie(CookieUtil.createCookie("Refresh","WakeDormantAccount"));
			response.setStatus(HttpStatus.OK.value());
			apiResponse = ApiResponse.success(new LoginResponse("휴면 계정"));
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json;charset=UTF-8");


		}else{
			response.addHeader("Authorization","Bearer "+access);
			response.addCookie(CookieUtil.createCookie("Refresh",refresh));
			response.setStatus(HttpStatus.OK.value());
			apiResponse = ApiResponse.success(new LoginResponse("인증 성공"));

			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json;charset=UTF-8");

		}

		response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
		// 인증 성공 시 응답 객체 생성
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());

		// TODO 프론트 서버에는 그냥 500 에러로 뜬다.
		// 인증 실패 시 응답 객체 생성
		// ApiResponse<ErrorResponseForm> apiResponse = ApiResponse.fail(HttpServletResponse.SC_UNAUTHORIZED,
		// 	new ApiResponse.Body<>(ErrorResponseForm.builder()
		// 		.title("이메일, 패스워드가 유효하지 않습니다.")
		// 		.status(HttpServletResponse.SC_UNAUTHORIZED)
		// 		.timestamp(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toString())
		// 		.build()));
		//
	}
}
