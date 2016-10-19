package com.github.bilak.oauth2.provider.security;

import com.github.bilak.oauth2.provider.persistence.jpa.model.Accessor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by lvasek on 14/10/2016.
 */
public class AccessorUserDetails implements UserDetails, Serializable {

	private String id;
	private String accountId;
	private String email;
	private String password;
	private String firstName;
	private String surname;
	private final String username;
	private final Set<GrantedAuthority> authorities;
	private final boolean accountNonExpired;
	private final boolean accountNonLocked;
	private final boolean credentialsNonExpired;
	private final boolean enabled;

	public AccessorUserDetails(Accessor accessor) {

		this(accessor.getId(),
				accessor.getAccountId(),
				accessor.getEmail(),
				accessor.getPassword(),
				accessor.getEmail(),
				new HashSet<>(AuthorityUtils.commaSeparatedStringToAuthorityList(accessor.getRole())),
				true,
				!accessor.getLocked(),
				true,
				accessor.getEnabled());
	}

	public AccessorUserDetails(String id, String accountId, String email, String password, String username,
			Set<GrantedAuthority> authorities, boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled) {
		this.id = id;
		this.accountId = accountId;
		this.email = email;
		this.password = password;
		this.username = username;
		this.authorities = authorities;
		this.accountNonExpired = accountNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.credentialsNonExpired = credentialsNonExpired;
		this.enabled = enabled;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public String getId() {
		return id;
	}

	public String getAccountId() {
		return accountId;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getSurname() {
		return surname;
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountId, email, password, firstName, surname, username, authorities, accountNonExpired, accountNonLocked, credentialsNonExpired,
				enabled);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final AccessorUserDetails other = (AccessorUserDetails) obj;
		return Objects.equals(this.accountId, other.accountId)
				&& Objects.equals(this.email, other.email)
				&& Objects.equals(this.password, other.password)
				&& Objects.equals(this.firstName, other.firstName)
				&& Objects.equals(this.surname, other.surname)
				&& Objects.equals(this.username, other.username)
				&& Objects.equals(this.authorities, other.authorities)
				&& Objects.equals(this.accountNonExpired, other.accountNonExpired)
				&& Objects.equals(this.accountNonLocked, other.accountNonLocked)
				&& Objects.equals(this.credentialsNonExpired, other.credentialsNonExpired)
				&& Objects.equals(this.enabled, other.enabled);
	}

	@Override
	public String toString() {
		return "AccessorUserDetails{" +
				"accountId='" + accountId + '\'' +
				", email='" + email + '\'' +
				", password= [PROTECTED]' " +
				", firstName='" + firstName + '\'' +
				", surname='" + surname + '\'' +
				", username='" + username + '\'' +
				", authorities=" + authorities +
				", accountNonExpired=" + accountNonExpired +
				", accountNonLocked=" + accountNonLocked +
				", credentialsNonExpired=" + credentialsNonExpired +
				", enabled=" + enabled +
				'}';
	}
}
