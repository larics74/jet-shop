package org.larics.jetshop.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/*
 * Security configuration.
 * 
 * @author Igor Laryukhin
 */
@Configuration
////enables method-level authorization
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final AuthenticationSuccessHandler authenticationSuccessHandler;

	@Autowired
	public SecurityConfig(UserDetailsService userDetailsService,
			AuthenticationSuccessHandler authenticationSuccessHandler) {
		this.userDetailsService = userDetailsService;
		this.authenticationSuccessHandler = authenticationSuccessHandler;
	}

	// Sets user authentication.
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(userDetailsService)
			.passwordEncoder(new BCryptPasswordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// for h2
		http.csrf().disable();
		http.headers().frameOptions().disable();

		http
			.authorizeRequests()
				.antMatchers("/css/**", "/images/**", 
						"/", "/help", "/factory").permitAll()	

				.antMatchers(HttpMethod.GET, "/register").not().authenticated()
				.antMatchers(HttpMethod.POST, "/register").not().authenticated()

				.antMatchers(HttpMethod.GET, "/jetModels").permitAll()
				.antMatchers(HttpMethod.GET, "/jetModels/{\\d+}").permitAll()
				.antMatchers(HttpMethod.GET, "/jetModels/drawings/{\\d+}")
					.permitAll()

				// for h2
				.antMatchers("/console/**").permitAll()		

				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/login").permitAll()
				.failureUrl("/login?error")
				.defaultSuccessUrl("/")
				.successHandler(authenticationSuccessHandler)
					.and()
				.logout().permitAll()
				.logoutSuccessUrl("/?logout")
		;
	}

}
