package com.smart.q.smartq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "payhere")
public class PayHereConfig {

	private boolean sandbox;
	private String sandboxMarchantId;
	private String sandboxMarchantSecret;
	private String returnUrl;
	private String cancelUrl;
	private String notifyUrl;
}
