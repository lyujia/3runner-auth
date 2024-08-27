package com.nhnacademy.auth.adapter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nhnacademy.auth.entity.MessagePayload;

@FeignClient(name = "doorayMessageClient",url = "https://hook.dooray.com/services")
public interface DoorayAdapter {
	@PostMapping("/{serviceId}/{botId}/{botToken}")
	String sendMessage(@RequestBody MessagePayload messagePayload,
	@PathVariable Long serviceId, @PathVariable Long botId, @PathVariable String botToken);
}

