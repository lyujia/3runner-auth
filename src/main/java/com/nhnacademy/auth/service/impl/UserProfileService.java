package com.nhnacademy.auth.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.nhnacademy.auth.entity.UserProfile;

import reactor.core.publisher.Mono;

@Service
public class UserProfileService {
	private final WebClient webClient;

	public UserProfileService(WebClient.Builder webClientBuilder){
		this.webClient = webClientBuilder.baseUrl("https://apis-payco.krp.toastoven.net").build();
	}

	public Mono<UserProfile> fetchUserProfile(String token){
		return webClient.get()
			.uri("/payco/friends/find_member_v2.json")
			.headers(headers->headers.setBearerAuth(token))
			.retrieve()
			.bodyToMono(UserProfile.class);
	}
}
