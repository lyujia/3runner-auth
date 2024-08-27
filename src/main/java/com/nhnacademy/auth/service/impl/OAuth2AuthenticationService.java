package com.nhnacademy.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OAuth2AuthenticationService {
	private final WebClient webClient;
	private final OAuth2AuthorizedClientManager authorizedClientManager;

	public OAuth2AccessToken getAccessTokenFromAuthorizationCode(String registrationId, String authorizationCode, Authentication principal, String redirectUri) {
		OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(registrationId)
			.attributes(attrs -> {attrs.put(OAuth2ParameterNames.CODE,authorizationCode);
			attrs.put(OAuth2ParameterNames.REDIRECT_URI,redirectUri);})
			.principal(principal).build();

		OAuth2AuthorizedClient authorizedClient = this.authorizedClientManager.authorize(authorizeRequest);

		if (authorizedClient != null) {
			return authorizedClient.getAccessToken();
		} else {
			throw new IllegalStateException("Unable to retrieve access token for client " + registrationId);
		}
	}

	public Mono<JsonNode> getToken(String code) {
		String url = "https://id.payco.com/oauth2.0/token?grant_type=authorization_code&client_id=3RDUR8qJyORVrsI2PdkInS1&client_secret=yoA1FPvf5ievEnC7LkzJDp1x&state=ab42ae&code=" + code;
		return webClient.get().uri(url).retrieve().bodyToMono(JsonNode.class);
	}

	public Mono<JsonNode> getUserDate(String clientId,String accessToken) {
		String url = "https://apis-payco.krp.toastoven.net/payco/friends/find_member_v2.json";
		return webClient.post().uri(url).header("client_id", clientId).header("access_token", accessToken).retrieve().bodyToMono(JsonNode.class);
	}
}
