package com.axis.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.axis.accountCreation.Exceptions.UserAlreadyExistException;
import com.axis.accountCreation.accountService.AccountService;
import com.axis.auth.AuthenticationResponse;
import com.axis.auth.RegisterRequest;

@RestController
@RequestMapping("/api/v1/manager")
@RequiredArgsConstructor
public class ManagerController {
	private final AccountService accountService;

    @GetMapping
    public String get() {
        return "GET:: management controller";
    }

    @PostMapping("/employeeregister")
    public ResponseEntity<AuthenticationResponse> employeeRegister(
        @RequestBody RegisterRequest request
    ) throws UserAlreadyExistException {
      return ResponseEntity.ok(accountService.createEmployee(request));
  }

    @PutMapping
    public String put() {
        return "PUT:: management controller";
    }
    @DeleteMapping
    public String delete() {
        return "DELETE:: management controller";
    }
}
