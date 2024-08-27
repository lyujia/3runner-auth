package com.nhnacademy.auth.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class MessagePayload {
	private String botName;
	private String botIconImage;
	private String text;
	private List<Attachment> attachments;

	@Data
	@NoArgsConstructor
	public static class Attachment {
		private String title= "test";
		private String titleLink = "http://naver.com";
		private String text = "text";
		private String color = "red";
	}
}