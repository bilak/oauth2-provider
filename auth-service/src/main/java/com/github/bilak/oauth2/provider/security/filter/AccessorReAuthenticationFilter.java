package com.github.bilak.oauth2.provider.security.filter;

import com.github.bilak.oauth2.provider.security.AccessorAuthenticationToken;
import liquibase.util.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

/**
 * Created by lvasek on 18/10/2016.
 */
public class AccessorReAuthenticationFilter implements Filter {

	private AuthenticationManager authenticationManager;

	public AccessorReAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		if (SecurityContextHolder.getContext() != null) {
			Principal principal = SecurityContextHolder.getContext().getAuthentication();
			if (principal != null) {
				if (principal.getClass().isAssignableFrom(OAuth2Authentication.class)) {
					final HttpServletRequest request = (HttpServletRequest) servletRequest;
					final HttpServletResponse response = (HttpServletResponse) servletResponse;
					OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
					// TODO check authorities -> allow only PRIVILEGED CLIENTS
					if (oAuth2Authentication.isClientOnly()) {
						final String cloudUserId = request.getHeader("X-USER-ID");
						if (StringUtils.isNotEmpty(cloudUserId)) {
							Authentication authentication = new AccessorAuthenticationToken(cloudUserId);
							authentication = authenticationManager.authenticate(authentication);
							SecurityContextHolder.getContext().setAuthentication(authentication);
						}
					}
				}
			}
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	@Override
	public void destroy() {

	}
}

