package com.clockwise.tworcy.configuration.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.clockwise.tworcy.model.account.AccountService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private @Autowired AccountService accounts;

	private @Autowired DataSource dataSource;

	public @Autowired void configureGlobalSecurity(
			AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(accounts).passwordEncoder(new BCryptPasswordEncoder());
	}
	
    public @Override void configure(WebSecurity webSecurity) throws Exception
    {
        webSecurity.ignoring().antMatchers("/resources/**");
    }

	protected @Override void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests()
				// Admins
				.antMatchers("/admin/**")
				.access("hasRole('ADMIN')")
				// Database admins
				.antMatchers("/db/**")
				.access("hasRole('ADMIN') and hasRole('DBA')")
				
				.and()
				// Login form settings
				.formLogin().loginPage("/account/login").loginProcessingUrl("/account/login")
				.usernameParameter("name")
				.passwordParameter("password")
				
				.and().csrf().and()
				// Remember me settings
				.rememberMe().rememberMeParameter("remember-me")
				.tokenRepository(persistentTokenRepository())
				.tokenValiditySeconds(86400).and().csrf()
				.and()
				.exceptionHandling().accessDeniedPage("/Access_Denied").accessDeniedHandler(accessDeniedHandler());
		

	    /*/ Logged in
	    .antMatchers(
	    		"/news/edit/", "/news/edit/**",
	    		"/game/edit/", "/game/edit/**")
	    .access("hasRole('NORMAL')")
	    .antMatchers(HttpMethod.POST,
	    		"/news/edit/", "/news/edit/**",
	    		"/game/edit/", "/game/edit/**")
	    .access("hasRole('NORMAL')")
	    // Admins
		.antMatchers("/admin/**")
		.access("hasRole('ADMIN')")
		// Database admins
		.antMatchers("/db/**")
		.access("hasRole('ADMIN') and hasRole('DBA')")*/
		
		// @formatter:on
		
		
	}

	private AccessDeniedHandler accessDeniedHandler() {
		return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                System.err.println(this.getClass().getSimpleName()+".accessDeniedHandler\n"+accessDeniedException.getMessage());
                // response.setStatus(HttpStatus.FORBIDDEN.value());
            }
        };
	}

	public @Bean PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl tokenRepositoryImpl = new JdbcTokenRepositoryImpl();
		tokenRepositoryImpl.setDataSource(dataSource);
		return tokenRepositoryImpl;
	}
}