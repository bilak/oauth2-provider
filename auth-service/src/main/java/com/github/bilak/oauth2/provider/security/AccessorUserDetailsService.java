package com.github.bilak.oauth2.provider.security;

import com.github.bilak.oauth2.provider.persistence.jpa.model.Accessor;
import com.github.bilak.oauth2.provider.persistence.jpa.repository.AccessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

/**
 * Created by lvasek on 16/10/2016.
 */
public class AccessorUserDetailsService implements UserDetailsService {

	private AccessorRepository accessorRepository;

	@Autowired
	public AccessorUserDetailsService(AccessorRepository accessorRepository) {
		this.accessorRepository = accessorRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Accessor> foundAccessor = null;
		if (username.contains("@")) {
			foundAccessor = accessorRepository.findOneByEmail(username);
		} else {
			foundAccessor = accessorRepository.findOneById(username);
		}
		return foundAccessor
				.map(accessor -> new AccessorUserDetails(accessor))
				.orElse(null);
	}
}
