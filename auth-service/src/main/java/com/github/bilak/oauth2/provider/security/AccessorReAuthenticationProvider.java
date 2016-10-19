package com.github.bilak.oauth2.provider.security;

import com.github.bilak.oauth2.provider.controller.schema.GetUserResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lvasek on 19/10/2016.
 */
public class AccessorReAuthenticationProvider implements AuthenticationProvider {

	private OAuth2RestOperations restTemplate;
	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	public AccessorReAuthenticationProvider(OAuth2RestOperations restTemplate) {
		Assert.notNull(restTemplate, "RestTemplate must be provided");
		this.restTemplate = restTemplate;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String userId = authentication.getPrincipal().getClass().isAssignableFrom(AccessorUserDetails.class) ?
				((AccessorUserDetails) authentication.getPrincipal()).getId() :
				authentication.getPrincipal().toString();

		ResponseEntity<GetUserResponse> getUserResponse =
				restTemplate.exchange(
						"http://localhost:9090/users/{userId}",
						HttpMethod.GET,
						null,
						GetUserResponse.class,
						userId);
		if (HttpStatus.OK.equals(getUserResponse.getStatusCode())) {
			GetUserResponse user = getUserResponse.getBody();
			Set<GrantedAuthority> authorities =
					StringUtils.isNotEmpty(user.getRole()) ?
							new HashSet<>(AuthorityUtils.createAuthorityList())
							: new HashSet<>();
			AccessorUserDetails userDetails = new AccessorUserDetails(
					user.getId(),
					user.getAccountId(),
					user.getEmail(),
					null,
					user.getEmail(),
					authorities,
					true,
					!user.getLocked(),
					true,
					user.getEnabled())
					.setFirstName(user.getFirstName())
					.setSurname(user.getSurname());
			return authenticate(userDetails);
		} else {
			throw new BadCredentialsException("Bad credentials");
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return AccessorAuthenticationToken.class.equals(authentication);
	}

	public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
		Assert.notNull(authoritiesMapper);
		this.authoritiesMapper = authoritiesMapper;
	}

	private AccessorAuthenticationToken authenticate(AccessorUserDetails userDetails) {
		handleIsAccountNotExpired(userDetails.isAccountNonExpired());
		handleIsAccountNotLocked(userDetails.isAccountNonLocked());
		handleIsCredentialsNonExpired(userDetails.isCredentialsNonExpired());
		handleIsEnabled(userDetails.isEnabled());

		return new AccessorAuthenticationToken(userDetails, authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

	}

	private void handleIsAccountNotExpired(Boolean isAccountNotExpired) {
		if (isAccountNotExpired != null && !isAccountNotExpired)
			throw new AccountExpiredException("Account is expired");
	}

	private void handleIsAccountNotLocked(Boolean isAccountNotLocked) {
		if (isAccountNotLocked != null && !isAccountNotLocked)
			throw new LockedException("Account is locked");
	}

	private void handleIsCredentialsNonExpired(Boolean isCredentialsNonExpired) {
		if (isCredentialsNonExpired != null && !isCredentialsNonExpired)
			throw new CredentialsExpiredException("Credentials expired");
	}

	private void handleIsEnabled(Boolean isEnabled) {
		if (isEnabled != null && !isEnabled)
			throw new DisabledException("Account is disabled");
	}

}
