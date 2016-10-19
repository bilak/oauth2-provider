package com.github.bilak.oauth2.provider.controller;

import com.github.bilak.oauth2.provider.C;
import com.github.bilak.oauth2.provider.controller.schema.GetUserResponse;
import com.github.bilak.oauth2.provider.persistence.jpa.repository.AccessorRepository;
import com.github.bilak.oauth2.provider.security.AccessorUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by lvasek on 16/10/2016.
 */
@RestController
@RequestMapping("/users")
public class UserController {

	private AccessorRepository accessorRepository;

	@Autowired
	public UserController(AccessorRepository accessorRepository) {
		this.accessorRepository = accessorRepository;
	}

	@GetMapping("/me")
	public Map<String, String> userInfo(Principal principal) {
		Map<String, String> map = new LinkedHashMap<>();
		if (principal != null) {
			if (principal.getClass().isAssignableFrom(OAuth2Authentication.class)) {
				OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
				if (!oAuth2Authentication.isClientOnly()) {
					if (oAuth2Authentication.getPrincipal().getClass().isAssignableFrom(AccessorUserDetails.class)) {
						AccessorUserDetails user = (AccessorUserDetails) oAuth2Authentication.getPrincipal();
						map.put(C.User.ACCOUNT_ID, user.getAccountId());
						map.put(C.User.EMAIL, user.getEmail());
						map.put(C.User.FIRST_NAME, user.getFirstName());
						map.put(C.User.SURNAME, user.getSurname());
					} else {
						throw new RuntimeException("Which kind of authentication is used?");
					}
				} else {
					map.put("name", principal.getName());
					map.put("client", "true");
				}
			} else {
				map.put("name", principal.getName());
			}
		}
		return map;
	}

	@GetMapping("/{userId}")
	public ResponseEntity<GetUserResponse> getUserById(@PathVariable("userId") String userId) {
		return Optional.ofNullable(accessorRepository.findOne(userId))
				.map(a ->
						new ResponseEntity<>(
								new GetUserResponse()
										.setEmail(a.getEmail())
										.setAccountId(a.getAccountId())
										.setEnabled(a.getEnabled())
										.setFirstName(a.getFirstName())
										.setId(a.getId())
										.setLocked(a.getLocked())
										.setSurname(a.getSurname())
										.setRole(a.getRole()),
								HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@Autowired
	private OAuth2RestOperations restTemplate;

	@GetMapping("/test/{userId}")
	public ResponseEntity<GetUserResponse> test(@PathVariable("userId") String userId) {
		return restTemplate
				.exchange("http://localhost:9090/users/{userId}", HttpMethod.GET, null, GetUserResponse.class, userId);
	}

}
