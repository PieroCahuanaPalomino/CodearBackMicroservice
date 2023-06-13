package com.codearti.filters.security.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

@Configuration
public class SpringSecurityConfiguration {

	@Bean
	public SecurityWebFilterChain configure(ServerHttpSecurity http) {
		
//		http.authorizeExchange(exchanges -> exchanges.anyExchange().authenticated())
//		.oauth2Login(Customizer.withDefaults()); //for SSO login with oauth server

//		http.authorizeExchange()
//		.pathMatchers("/actuator/**").permitAll()
//		.anyExchange().authenticated()
//		.and()
//		.oauth2Login(Customizer.withDefaults());
		
		http.authorizeExchange()
		.pathMatchers("/actuator/**", "/api/security/**").permitAll()
		.pathMatchers(HttpMethod.GET, "/api/cart/**", "/api/product/**").hasAnyRole("USER", "ADMIN")
		.pathMatchers("/api/cart/**", "/api/product/**", "/api/keycloak/**").hasAnyRole("ADMIN")
		.anyExchange().authenticated()
		.and()
		//.oauth2Login(Customizer.withDefaults()) //for SSO login with oauth server
		//.oauth2ResourceServer(OAuth2ResourceServerSpec::jwt) //for without RBAC
		.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesConverter())))
		.logout()
		; //for RBAC
		
		return http.build();
	}
	
	private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesConverter() {
	    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
	    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new JwtGrantedAuthoritiesConverter());
	    return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
	}
	
}
