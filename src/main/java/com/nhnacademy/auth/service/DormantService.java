package com.nhnacademy.auth.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.nhnacademy.auth.entity.DormantObject;

import lombok.extern.slf4j.Slf4j;

public interface DormantService {
	public String saveVerificationCode(String email, String access, String refresh);
	public DormantObject getVerificationCode(String memberId);
	public DormantObject checkVerificationCode(String memberId, String verificationCode);
	public DormantObject updateVerificationCode(String email);
}

