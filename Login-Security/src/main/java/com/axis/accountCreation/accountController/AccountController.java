package com.axis.accountCreation.accountController;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

import com.axis.accountCreation.Exceptions.AccountCannotCloseException;
import com.axis.accountCreation.Exceptions.AccountNotFoundException;
import com.axis.accountCreation.Exceptions.UserAlreadyExistException;
import com.axis.accountCreation.accountService.AccountOverviewResponse;
import com.axis.accountCreation.accountService.AccountService;
import com.axis.accountCreation.model.Account;
import com.axis.accountCreation.model.AccountStatus;
import com.axis.accountCreation.pdfGenerator.PDFGenerator;
import com.axis.auth.AuthenticationResponse;
import com.axis.auth.RegisterRequest;
import com.axis.config.JwtService;
import com.axis.service.UserServiceImplem;
import com.axis.user.Role;
import com.axis.user.User;
import com.axis.userrepository.UserRepository;
import com.lowagie.text.DocumentException;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/accounts")
//@PreAuthorize("hasRole('ADMIN')")

public class AccountController {

    private final AccountService accountService;
    private final JwtService jwtService;
    private final UserServiceImplem userServiceImplem;
    
    @Autowired
    public AccountController(AccountService accountService, JwtService jwtService,UserServiceImplem userServiceImplem) {
        this.accountService = accountService;
        this.jwtService = jwtService;
        this.userServiceImplem=userServiceImplem;
    }

    /**
     * This controller fetches the user account overview by getting the userId from the JWT token
     */
//    @GetMapping("/overview")
//    public ResponseEntity<ApiResponse> getUserAccountOverview(
//                    @RequestHeader("Authorization") String jwt) {
//        try {
//            AccountOverviewResponse response = accountService.generateAccountOverviewByUserId(
//                    jwtService.extractUsername(jwt));
//            return new ResponseEntity<>(new ApiResponse("user account overview", response),
//                    HttpStatus.OK);
//        } catch (ResourceNotFoundException e) {
//            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.NOT_FOUND);
//        }
//
//    }
//    
    /**
     * This controller allows user to close their account by getting the userId from the JWT and the relieving reason
     * from the request body
     */
//    @DeleteMapping("/close")
//    public ResponseEntity<ApiResponse> closeAccount(
//            @RequestHeader("Authorization") String jwt) {
//            accountService.closeAccount(jwtService.extractUsername(jwt));
//            return new ResponseEntity<>(new ApiResponse("account closed successfully"), HttpStatus.OK);
//    }
    @PostMapping("/profile")
    public ResponseEntity<String> createAccount(@RequestBody Account account) {
        accountService.createAccount(account);
        return new ResponseEntity<>("Account created", HttpStatus.CREATED);
    }
    @GetMapping("/all")
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountByUserId(@PathVariable("id") String accountNumber) {
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        if (account != null) {
            return new ResponseEntity<>(account, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable("id") String accontNumber, @RequestBody Account account) {
        Account updatedAccount = accountService.updateAccount(accontNumber, account);
        if (updatedAccount != null) {
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable("id") String accountNumber) throws  AccountCannotCloseException {
       
        return new ResponseEntity<String>( accountService.closeAccount( accountNumber), HttpStatus.NO_CONTENT);
    }
    @GetMapping("/overview/{id}")
    public ResponseEntity<AccountOverviewResponse>getAllDetailsOfAccount(@PathVariable String AccountNumber){
    	return new ResponseEntity<AccountOverviewResponse>(accountService.generateAccountOverviewByUserId(AccountNumber),HttpStatus.OK);
    }
    
    @GetMapping("/allusersdemo")
    public List<User> listAll(){
    	return userServiceImplem.getAllUsers();
    }
    
    @GetMapping("/userdemo/{id}")
    public User findbyId(@PathVariable int id) {
    	return userServiceImplem.getUserByID(id);
    }

    @GetMapping("/pdf/accounts")
	public void generatePdf(HttpServletResponse response) throws DocumentException, IOException, java.io.IOException {
		
		response.setContentType("application/pdf");
		DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD:HH:MM:SS");
		String currentDateTime = dateFormat.format(new Date(00, 00, 00));
		String headerkey = "Content-Disposition";
		String headervalue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
		response.setHeader(headerkey, headervalue);
		
		List<Account> accountList = accountService.getAllAccounts();
		
		PDFGenerator generator = new PDFGenerator();
		 generator.setAccountList(accountList);
		generator.generate(response);
		
	}

    
  }
