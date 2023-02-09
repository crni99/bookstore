package com.crni99.bookstore.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private DataSource securityDataSource;
	
	public SecurityConfig(DataSource securityDataSource) {
		this.securityDataSource = securityDataSource;
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.jdbcAuthentication().dataSource(securityDataSource);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable()
			.authorizeRequests()
			.antMatchers("/").permitAll()
			.antMatchers("/h2-console").permitAll()
			.antMatchers("/search").permitAll()
			.antMatchers("/cart/**").permitAll()
			.antMatchers("/book/**").hasAuthority("ADMIN")
			.antMatchers("/orders/**").hasAuthority("ADMIN")
			.and()
			.formLogin()
			.loginPage("/login")
			.permitAll()
			.and()
			.logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/");
		
		// H2-Console enable
		http.headers().frameOptions().disable();
	}

}
