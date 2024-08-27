package com.nhnacademy.auth.service.impl;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.nhnacademy.auth.adapter.DoorayAdapter;
import com.nhnacademy.auth.entity.DormantObject;
import com.nhnacademy.auth.entity.MessagePayload;
import com.nhnacademy.auth.service.DormantService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@RequiredArgsConstructor
public class DormantServiceImpl implements DormantService {
	private static final String MEMBER_PREFIX = "email:";

	private final RedisTemplate<String, Object> dormantTemplate;
	private final DoorayAdapter doorayAdapter;

	public String saveVerificationCode(String email, String access, String refresh) {
		String key = MEMBER_PREFIX + email;
		DormantObject dormantObject = new DormantObject();
		dormantObject.setUuid(null);
		dormantObject.setAccess(access);
		dormantObject.setRefresh(refresh);
		dormantTemplate.opsForValue().set(key, dormantObject);
		log.info("Verification code saved for email: {}", email);

		return null;
	}

	public DormantObject getVerificationCode(String email) {
		String key = MEMBER_PREFIX + email;
		if (dormantTemplate.opsForValue().get(key) instanceof DormantObject) {
			return (DormantObject) dormantTemplate.opsForValue().get(key);
		}
		return null;
	}
	public DormantObject checkVerificationCode(String email, String verificationCode) {
		DormantObject dormantObject = getVerificationCode(email);
		if (dormantObject.getUuid().equals(verificationCode)) {
			return dormantObject;
		}
		return null;
	}

	public DormantObject updateVerificationCode(String email) {
		String key = MEMBER_PREFIX + email;
		DormantObject existingDormantObject = getVerificationCode(email);
		if (existingDormantObject != null) {
			// Update UUID and reset expiration time
			String uuid = UUID.randomUUID().toString();
			existingDormantObject.setUuid(uuid);

			MessagePayload messagePayload = new MessagePayload("인증번호", "", uuid, null);
			String string = doorayAdapter.sendMessage(messagePayload,3204376758577275363L,3844395046544334636L,"7Rh_SFmHQAmzRkK7ClGacw");

			dormantTemplate.opsForValue().set(key, existingDormantObject, 3, TimeUnit.MINUTES);
			log.info("Verification code updated for email: {}", email);

			return existingDormantObject;
		}
		return null;
	}
}

