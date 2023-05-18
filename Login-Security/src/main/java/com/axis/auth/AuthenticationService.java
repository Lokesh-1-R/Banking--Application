package com.axis.auth;

import com.axis.user.Role;
import com.axis.user.User;
import com.axis.userrepository.UserRepository;
import com.axis.accountCreation.accountService.AccountOverviewResponse;
import com.axis.accountCreation.accountService.AccountService;
import com.axis.accountCreation.model.Account;
import com.axis.accountCreation.model.AccountStatus;
import com.axis.accountCreation.model.AccountType;
import com.axis.config.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.hibernate.query.NativeQuery.ReturnableResultNode;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final AccountService accountService;

  public AuthenticationResponse register(RegisterRequest request) {
  Optional<User> email = repository.findByEmail(request.getEmail());
	  
	  if(request.getRole() == null) {
		  request.setRole(Role.USER);
	  }
	  
	
	  var user = User.builder()
    		
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .build();
	
	  {  
		    var savedUser = repository.save(user);
	  }

    Account account;
    int id;
    id =user.getId();

    if(!(request.getRole() == Role.ADMIN || request.getRole() == Role.MANAGER))
    {
         id =user.getId();

    		 account = accountService.DefaultcreateAccount(id);
    }
    var jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .build();
	  }
	  
  
	 
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .build();
  }
  

  

    }
  

