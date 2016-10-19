package com.github.bilak.oauth2.provider.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by lvasek on 16/10/2016.
 */
@Configuration
public class H2ServerConfiguration {

	@Value("${spring.h2.tcp.port:9091}")
	private String tcpPort;

	@Bean
	ServletListenerRegistrationBean h2ServletListener() {
		ServletListenerRegistrationBean listener = new ServletListenerRegistrationBean(new H2ServletListener(tcpPort));
		return listener;
	}
}
