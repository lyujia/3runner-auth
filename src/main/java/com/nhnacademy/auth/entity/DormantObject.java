package com.nhnacademy.auth.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@RequiredArgsConstructor
public class DormantObject {
	private String uuid;
	private String access;
	private String refresh;
}


