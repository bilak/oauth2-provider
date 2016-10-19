package com.github.bilak.oauth2.provider.controller.schema;

/**
 * Created by lvasek on 18/10/2016.
 */
public class GetUserResponse {

	private String id;
	private String accountId;
	private String email;
	private String firstName;
	private String surname;
	private Boolean locked;
	private Boolean enabled;
	private String role;

	public String getId() {
		return id;
	}

	public GetUserResponse setId(String id) {
		this.id = id;
		return this;
	}

	public String getAccountId() {
		return accountId;
	}

	public GetUserResponse setAccountId(String accountId) {
		this.accountId = accountId;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public GetUserResponse setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public GetUserResponse setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getSurname() {
		return surname;
	}

	public GetUserResponse setSurname(String surname) {
		this.surname = surname;
		return this;
	}

	public Boolean getLocked() {
		return locked;
	}

	public GetUserResponse setLocked(Boolean locked) {
		this.locked = locked;
		return this;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public GetUserResponse setEnabled(Boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public String getRole() {
		return role;
	}

	public GetUserResponse setRole(String role) {
		this.role = role;
		return this;
	}

	@Override
	public String toString() {
		return "GetUserResponse{" +
				"id='" + id + '\'' +
				", accountId='" + accountId + '\'' +
				", email='" + email + '\'' +
				", firstName='" + firstName + '\'' +
				", surname='" + surname + '\'' +
				", locked=" + locked +
				", enabled=" + enabled +
				", role='" + role + '\'' +
				'}';
	}
}
