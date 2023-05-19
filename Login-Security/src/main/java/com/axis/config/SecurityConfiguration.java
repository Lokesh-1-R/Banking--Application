package com.axis.config;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.axis.user.Permission.*;

import static com.axis.user.Role.*;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf()
        .disable()
        .authorizeHttpRequests()
        .requestMatchers(
                "/api/v1/auth/authenticate",
                "/api/v1/auth/register",
                "/api/v1/accounts/pdf/**"
//                "/api/v1/accounts/**"
                
        )
          .permitAll()

          .requestMatchers("/api/v1/accounts/all","/api/v1/accounts/{id}","/api/v1/accounts/user/{id}").hasAnyRole(ADMIN.name(), MANAGER.name() , EMPLOYEE.name())



          .requestMatchers(GET, "/api/v1/accounts/all","/api/v1/accounts/{id}","/api/v1/accounts/user/{id}").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name(),EMPLOYEE_READ.name())
          .requestMatchers(POST, "/api/v1/management/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name(),EMPLOYEE_CREATE.name())
          .requestMatchers(PUT, "/api/v1/management/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name(),EMPLOYEE_DELETE.name())
          .requestMatchers(DELETE, "/api/v1/management/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name(),EMPLOYEE_DELETE.name())



        .requestMatchers("/api/v1/accounts/allUsers","/api/v1/manager/employeeregister").hasAnyRole(ADMIN.name(), MANAGER.name())


        .requestMatchers(GET, "/api/v1/accounts/allUsers").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
        .requestMatchers(POST, "/api/v1/manager/employeeregister").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
        .requestMatchers(PUT, "/api/v1/management/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
        .requestMatchers(DELETE, "/api/v1/management/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())


      /*  .requestMatchers("/api/v1/accounts/userbyrole/{role}","/api/v1/auth/managerregister").hasRole(ADMIN.name())

        .requestMatchers(GET, "/api/v1/accounts/userbyrole/{role}").hasAuthority(ADMIN_READ.name())
        .requestMatchers(POST, "/api/v1/auth/managerregister").hasAuthority(ADMIN_CREATE.name())
        .requestMatchers(PUT, "/api/v1/accounts/update/{id}").hasAuthority(ADMIN_UPDATE.name())
        .requestMatchers(DELETE, "/api/v1/admin/**").hasAuthority(ADMIN_DELETE.name())

*/
        .anyRequest()
          .authenticated()
        .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .logout()
        .logoutUrl("/api/v1/auth/logout")
        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())

    ;

    return http.build();
  }
}
