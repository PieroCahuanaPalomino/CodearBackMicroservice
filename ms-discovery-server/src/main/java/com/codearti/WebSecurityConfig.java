package com.codearti;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig /*extends WebSecurityConfigurerAdapter*/{

//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//        .passwordEncoder(NoOpPasswordEncoder.getInstance())
//        .withUser("admin").password("admin")
//        .authorities("ADMIN");
//    }

////no funciona al 100%
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//            .csrf()
//                .disable()
//            .authorizeRequests()
//              .anyRequest().authenticated()
//              .and()
//              .httpBasic();
//    }
    
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().ignoringAntMatchers("/eureka/**");
//        super.configure(http);
//    }
    
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	    //.csrf().disable()
	    .csrf()
	    
	    .ignoringAntMatchers("/eureka/**")
	    
	    .and()
	    
	    .authorizeRequests()
	    
	    //.antMatchers(HttpMethod.DELETE).hasRole("ADMIN")
	    //.antMatchers("/admin/**").hasAnyRole("ADMIN")
	    //.antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
	    
	    .antMatchers("/login/**").anonymous()
	    
	    .anyRequest().authenticated()
	    .and()
	    .formLogin()
	    .and()
	    .httpBasic(withDefaults())
	    //.and()
	    //.sessionManagement()
	    //.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	    ;

	    return http.build();
	}
}