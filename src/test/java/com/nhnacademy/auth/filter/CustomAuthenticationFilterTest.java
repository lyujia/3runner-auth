// package com.nhnacademy.auth.filter;
//
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.anyLong;
// import static org.mockito.Mockito.anyString;
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.util.ArrayList;
// import java.util.Collection;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.MockitoAnnotations;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.context.annotation.Import;
// import org.springframework.http.MediaType;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.test.context.TestPropertySource;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.nhnacademy.auth.config.SecurityConfig;
// import com.nhnacademy.auth.dto.CustomUserDetails;
// import com.nhnacademy.auth.dto.request.LoginRequest;
// import com.nhnacademy.auth.util.JWTUtil;
//
// /**
//  * Custom Authentication Filter에 대한 테스트입니다.
//  *
//  * @author 오연수
//  */
// @SpringBootTest
// public class CustomAuthenticationFilterTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@MockBean
// 	private JWTUtil jwtUtil;
//
// 	@MockBean
// 	private AuthenticationManager authenticationManager;
//
// 	@Autowired
// 	private ObjectMapper objectMapper;
//
// 	@InjectMocks
// 	private CustomAuthenticationFilter customAuthenticationFilter;
//
// 	/**
// 	 * Sets up.
// 	 */
// 	@BeforeEach
// 	public void setUp() {
// 		MockitoAnnotations.openMocks(this);
// 	}
//
// 	@Test
// 	@DisplayName("인증이 성공적인 경우")
// 	void testAttemptAuthenticationSuccess() throws Exception {
// 		LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
//
// 		Authentication authentication = mock(Authentication.class);
// 		when(authenticationManager.authenticate(any())).thenReturn(authentication);
//
// 		CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
// 		when(authentication.getPrincipal()).thenReturn(customUserDetails);
// 		when(customUserDetails.getUsername()).thenReturn("test@example.com");
// 		when(customUserDetails.getMemberId()).thenReturn(1L);
//
// 		Collection<GrantedAuthority> collection = new ArrayList<>();
// 		collection.add((GrantedAuthority)() -> "ROLE_ADMIN");
//
// 		doReturn(collection).when(authentication).getAuthorities();
//
// 		when(jwtUtil.generateToken(anyString(), anyString(), anyString(), anyLong(), anyLong())).thenReturn("token");
//
// 		mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(loginRequest)))
// 			.andExpect(status().isOk())
// 			.andExpect(header().string("Authorization", "Bearer token"))
// 			.andExpect(cookie().value("Refresh", "token"))
// 			.andExpect(jsonPath("$.token").value("token"));
//
// 		verify(authenticationManager, times(1)).authenticate(any());
// 	}
//
// 	@Test
// 	@DisplayName("인증이 성공하지 못한 경우")
// 	void testAttemptAuthenticationFailure() throws Exception {
// 		LoginRequest loginRequest = new LoginRequest("test@example.com", "wrongpassword");
//
// 		when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException(""));
//
// 		mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(loginRequest)))
// 			.andExpect(status().isUnauthorized());
//
// 		verify(authenticationManager, times(1)).authenticate(any());
// 	}
// }
