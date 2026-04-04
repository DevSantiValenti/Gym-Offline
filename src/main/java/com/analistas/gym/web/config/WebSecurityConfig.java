package com.analistas.gym.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity (
	prePostEnabled = true,
	securedEnabled = true
)
public class WebSecurityConfig {

    @Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/", "/home", "/login", "/error/", "/css/**","/js/**","/img/**").permitAll()
				.anyRequest().authenticated())
			.formLogin((form) -> form
				.loginPage("/login")
				.defaultSuccessUrl("/home", true)
				.permitAll())
			.exceptionHandling(exception -> exception
            	.accessDeniedPage("/error/")
        )
			.logout((logout) -> logout.permitAll()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login"));

		return http.build();
	}

    @Bean
	public UserDetailsService userDetailsService() {
	    		UserDetails carlosFrete = User.withDefaultPasswordEncoder()
				.username("Carlos Fretes")
				.password("halcongym2026")
				.roles("ADMIN")
				.build();

                UserDetails general = User.withDefaultPasswordEncoder()
				.username("HALCONGYM")
				.password("12345678")
				.roles("USER")
				.build();

				UserDetails lucianoFrete = User.withDefaultPasswordEncoder()
				.username("Luciano Fretes")
				.password("halcongym2026")
				.roles("ADMIN")
				.build();

				UserDetails superadmin = User.withDefaultPasswordEncoder()
				.username("AdminSanti")
				.password("chuflitos1")
				.roles("ADMIN")
				.build();

				// UserDetails administrador2 = User.withDefaultPasswordEncoder()
				// .username("Administrador2")
				// .password("administrador2")
				// .roles("ADMIN")
				// .build();

				// UserDetails usuario2 = User.withDefaultPasswordEncoder()
				// .username("Usuario2")
				// .password("usuario2")
				// .roles("USER")
				// .build();

		return new InMemoryUserDetailsManager(carlosFrete, general, lucianoFrete, superadmin);
	}

}
