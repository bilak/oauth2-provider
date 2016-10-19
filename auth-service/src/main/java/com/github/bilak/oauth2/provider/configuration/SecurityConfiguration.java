package com.github.bilak.oauth2.provider.configuration;

import com.github.bilak.oauth2.provider.persistence.jpa.repository.AccessorRepository;
import com.github.bilak.oauth2.provider.security.AccessorReAuthenticationProvider;
import com.github.bilak.oauth2.provider.security.AccessorUserDetailsService;
import com.github.bilak.oauth2.provider.security.filter.AccessorReAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import org.springframework.security.web.session.SessionManagementFilter;

import javax.servlet.Filter;
import java.util.Arrays;

/**
 * Created by lvasek on 06/10/2016.
 */
@Configuration
public class SecurityConfiguration {

	@Autowired
	private AccessorRepository accessorRepository;

	@Bean
	UserDetailsService userDetailsService() {
		return new AccessorUserDetailsService(accessorRepository);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	OAuth2RestOperations privilegedRestTemplate(ResourceServerProperties properties) {
		AccessTokenRequest atr = new DefaultAccessTokenRequest();
		return new OAuth2RestTemplate(clientResourceDetails(properties), new DefaultOAuth2ClientContext(atr));
	}

	private OAuth2ProtectedResourceDetails clientResourceDetails(ResourceServerProperties properties) {
		ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
		resource.setAccessTokenUri("http://localhost:9090/oauth/token");
		resource.setClientId("privileged");
		resource.setClientSecret("privileged");
		resource.setScope(Arrays.asList("read"));
		return resource;
	}

	@Configuration
	@EnableAuthorizationServer
	public static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		@Autowired
		private AuthenticationManager authenticationManager;

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.authenticationManager(authenticationManager);
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			// @Formatter:off
			clients
					.inMemory()
						.withClient("demo")
						.secret("demo")
						.scopes("read", "write", "trust")
						//.resourceIds("test")
						//.authorities("ROLE_APP")
						.authorizedGrantTypes("authorization_code", "client_credentials", "refresh_token", "implicit", "password")
					.and()
						.withClient("privileged")
						.secret("privileged")
						.scopes("read", "write")
						.authorizedGrantTypes("client_credentials");
			// @Formatter:on
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
			security
					.tokenKeyAccess("permitAll()")
					.checkTokenAccess("isAuthenticated()");
		}
	}

	@Configuration
	@EnableResourceServer
	public static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		@Autowired
		private AuthenticationManager authenticationManager;

		Filter customFilter() {
			AccessorReAuthenticationFilter accessorReAuthenticationFilter = new AccessorReAuthenticationFilter(authenticationManager);
			return accessorReAuthenticationFilter;
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			// @Formatter:off
					http
						.antMatcher("/users/**")
						.authorizeRequests().anyRequest().authenticated()
					.and()
					.addFilterBefore(customFilter(), SessionManagementFilter.class);
			// @Formatter:on
		}
	}

	@Configuration
	@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
	public static class OAuth2ResourceServerConfig extends GlobalMethodSecurityConfiguration {
		@Override
		protected MethodSecurityExpressionHandler createExpressionHandler() {
			return new OAuth2MethodSecurityExpressionHandler();
		}

	}

	@Configuration
	public static class AuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter {

		private UserDetailsService userDetailsService;
		private PasswordEncoder passwordEncoder;
		private OAuth2RestOperations restTemplate;

		@Autowired
		public AuthenticationConfiguration(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, OAuth2RestOperations restTemplate) {
			this.userDetailsService = userDetailsService;
			this.passwordEncoder = passwordEncoder;
			this.restTemplate = restTemplate;
		}

		@Bean
		AuthenticationProvider accessorReAuthenticationProvider() {
			AccessorReAuthenticationProvider provider = new AccessorReAuthenticationProvider(restTemplate);
			return provider;
		}

		@Override
		public void init(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService)
					.passwordEncoder(passwordEncoder)
					.and()
					.authenticationProvider(accessorReAuthenticationProvider());
		}
	}

	@Configuration
	@Order(6)
	public static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

		@Value("${spring.h2.console.path:/h2-console}")
		private String h2ConsolePath;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
					.formLogin().loginPage("/login").permitAll()
					.and()
					.requestMatchers()
					.antMatchers("/", "/login", "/oauth/authorize", "/oauth/confirm_access", h2ConsolePath)
					.and()
					.authorizeRequests()
					.anyRequest().authenticated();
		}

	}
}