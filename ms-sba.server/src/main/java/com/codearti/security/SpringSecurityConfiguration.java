package com.codearti.security;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import de.codecentric.boot.admin.server.config.AdminServerProperties;

@Configuration
public class SpringSecurityConfiguration {

	private static final String REDIRECT_TO = "redirectTo";
	private static final String ASSETS = "/assets/**";
	private static final String LOGIN = "/login";
	private static final String LOGOUT = "/logout";

	private final String adminContextPath;

	public SpringSecurityConfiguration(AdminServerProperties adminServerProperties) {
		this.adminContextPath = adminServerProperties.getContextPath();
	}

	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {

		SavedRequestAwareAuthenticationSuccessHandler objSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
		objSuccessHandler.setTargetUrlParameter(REDIRECT_TO);

		http.authorizeRequests().antMatchers(this.adminContextPath + ASSETS).permitAll()
				.antMatchers(this.adminContextPath + LOGIN).permitAll().requestMatchers(EndpointRequest.toAnyEndpoint())
				.permitAll().anyRequest().authenticated().and().formLogin().loginPage(this.adminContextPath + LOGIN)
				.successHandler(objSuccessHandler).and().logout().logoutUrl(this.adminContextPath + LOGOUT).and()
				.httpBasic().and().csrf().disable();
		return http.build();
	}

}
