package com.github.bilak.oauth2.provider.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by lvasek on 19/10/2016.
 */
public class AccessorAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = -5964307097221600980L;

	private final Object principal;

	public AccessorAuthenticationToken(Object principal) {
		super(null);
		this.principal = principal;
	}

	public AccessorAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}
}
