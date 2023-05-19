package com.axis.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.axis.accountCreation.Exceptions.UserAlreadyExistException;
import com.axis.accountCreation.accountService.AccountService;
import com.axis.auth.AuthenticationResponse;
import com.axis.auth.RegisterRequest;
import com.axis.config.JwtService;
import com.axis.service.UserServiceImplem;
import com.axis.user.Role;
import com.axis.user.User;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	 private final AccountService accountService;
	    private final JwtService jwtService;
	    private final UserServiceImplem userServiceImplem;
	    
	    @Autowired
	    public AdminController(AccountService accountService, JwtService jwtService,UserServiceImplem userServiceImplem) {
	        this.accountService = accountService;
	        this.jwtService = jwtService;
	        this.userServiceImplem=userServiceImplem;
	    }
	    

	   @GetMapping("/userbyrole/{role}")
    @PreAuthorize("hasAuthority('admin:read')")
    public List<User> getUserByRole(@PathVariable Role role) {
		return  accountService.getUserByRole(role);
    	

    }
    @PostMapping("/managerregister")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<AuthenticationResponse> managerRegister(
        @RequestBody RegisterRequest request
    ) throws UserAlreadyExistException {
      return ResponseEntity.ok(accountService.createManager(request));
  }
    @PutMapping
    @PreAuthorize("hasAuthority('admin:update')")
    public String put() {
        return "PUT:: admin controller";
    }
    @DeleteMapping
    @PreAuthorize("hasAuthority('admin:delete')")
    public String delete() {
        return "DELETE:: admin controller";
    }
}
