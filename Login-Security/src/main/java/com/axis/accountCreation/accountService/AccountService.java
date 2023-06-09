package com.axis.accountCreation.accountService;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.axis.accountCreation.Exceptions.AccountCannotCloseException;
import com.axis.accountCreation.Exceptions.AccountNotActivatedException;
import com.axis.accountCreation.Exceptions.AccountNotFoundException;
import com.axis.accountCreation.Exceptions.ResourceNotFoundException;
import com.axis.accountCreation.Exceptions.UserAlreadyExistException;
import com.axis.accountCreation.accountrepository.AccountRepository;
import com.axis.accountCreation.model.Account;
import com.axis.accountCreation.model.AccountStatus;
import com.axis.accountCreation.model.AccountType;
import com.axis.auth.AuthenticationResponse;
import com.axis.auth.RegisterRequest;
import com.axis.config.JwtService;
import com.axis.user.Role;
import com.axis.user.User;
import com.axis.userrepository.UserRepository;

@Service
public class AccountService {
	private final AccountRepository accountRepository;
	private final UserRepository userRepo;
	private PasswordEncoder passwordEncoder;
	private JwtService jwtService;
    @Autowired
    public AccountService(AccountRepository accountRepository,UserRepository userRepo,PasswordEncoder passwordEncoder ,JwtService jwtService) {
        this.accountRepository = accountRepository;
        this.userRepo = userRepo;
        this.passwordEncoder=passwordEncoder;
        this.jwtService= jwtService;
    }

    @Async
    public void createAccount(Account account) {
        account.setAccountNumber(generateUniqueAccountNumber());
        if(account.getAccountType()==AccountType.CURRENT_ACCOUNT) {
            account.setAccountType(AccountType.CURRENT_ACCOUNT);

        }else {
            account.setAccountType(AccountType.SAVINGS_ACCOUNT);
	
        }
        account.setAccountStatus(AccountStatus.ACTIVATED);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        
        
        accountRepository.save(account);
    }
    
    public Account DefaultcreateAccount() {
    	Account account = new Account();
        account.setAccountNumber(generateUniqueAccountNumber());
            account.setAccountType(AccountType.CURRENT_ACCOUNT);

       
	
        
        account.setAccountStatus(AccountStatus.PENDING);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        
        
       return accountRepository.save(account);
    }
    private String generateUniqueAccountNumber() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int accountNumber;
        boolean exists;
        do {
            accountNumber = random.nextInt(1_000_000_000);
            exists = accountRepository.existsByAccountNumber(String.format("%09d", accountNumber));
        } while (exists);
        return String.format("%09d", accountNumber);
    }
    public Account getAccountByAccountNumber(String accountNumber) {
        Optional<Account> account = accountRepository.findAccountByAccountNumber(accountNumber);
        if(!account.isPresent()){
            throw new ResourceNotFoundException("account not found");
        }
        return account.get();
    }


    public AccountOverviewResponse generateAccountOverviewByUserId(String accountNumber){
        Account userAccount = getAccountByAccountNumber(accountNumber);
        return new AccountOverviewResponse(
                userAccount.getAccountBalance(),
                userAccount.getAccountNumber(),
                userAccount.getAccountType().name(),
                userAccount.getAccountStatus().name()
        );
    }
 public Account updateAccount(String accountNumber , Account existingAccount) {
    	
    	Account acc = accountRepository.findAccountByAccountNumber(accountNumber).get();
    	acc.setAccountBalance(existingAccount.getAccountBalance());
    	acc.setAccountStatus(existingAccount.getAccountStatus());
    	acc.setAccountType(existingAccount.getAccountType());
        acc.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(acc);
        
        return acc;
    }
    				    
    
   
   
   /* 
    public void creditAccount(String receiverAccountNumber,double amount) throws AccountNotFoundException {
    Account account = 	accountRepository.findAccountByAccountNumber(receiverAccountNumber).get();
    	double oldbalance = account.getAccountBalance() ;
    	double currentbalance = oldbalance + amount;
        account.setAccountBalance(currentbalance);
        account.setUpdatedAt(LocalDateTime.now());
        
        accountRepository.save(account);
        
    }
    
   */
    
    
    public String closeAccount(String AccountNumber) throws AccountCannotCloseException {
    	Account account = accountRepository.findAccountByAccountNumber(AccountNumber).get();
    	if(account.getAccountBalance() == 0)
    	{
    	account.setAccountStatus(AccountStatus.PENDING);
    	accountRepository.save(account);
    	return "Account will be closed after verification";
    }
    	else {
			throw new AccountCannotCloseException("Account cannot be closed , unless the balance is zero ");
		}
    }
    
    
	
	
	public List<User>getAllUsers(){
		return userRepo.findAll();
	}
	public User getUserByID(int id) {
		return userRepo.findById(id).get();
	}

	public List<Account> getAllAccounts() {
		// TODO Auto-generated method stub
		return accountRepository.findAll();
	}
	public List<User> getUserByRole(Role role){
		return userRepo.findByRole(role);
	}

	
	public AuthenticationResponse createManager(RegisterRequest request) throws UserAlreadyExistException {
		Optional<User>emailExist = userRepo.findByEmail(request.getEmail());
		 var manager = User.builder()
		    		
			        .firstname(request.getFirstname())
			        .lastname(request.getLastname())
			        .email(request.getEmail())
			        .password(passwordEncoder.encode(request.getPassword()))
			        .role(Role.MANAGER)
			        .build();
				  	if(emailExist.isPresent()){
				  		throw new UserAlreadyExistException("manager with " + emailExist.get().getEmail() + " is already exists ");
				  		
				  	}
				  	else
				  	{
					    userRepo.save(manager);
				  	}
				  	 var jwtToken = jwtService.generateToken(manager);
				     return AuthenticationResponse.builder()
				         .accessToken(jwtToken)
				         .build();
		
	}

	public AuthenticationResponse createEmployee(RegisterRequest request) throws UserAlreadyExistException {
		// TODO Auto-generated method stub
		Optional<User>emailExist = userRepo.findByEmail(request.getEmail());
		 var employee = User.builder()
		    		
			        .firstname(request.getFirstname())
			        .lastname(request.getLastname())
			        .email(request.getEmail())
			        .password(passwordEncoder.encode(request.getPassword()))
			        .role(Role.EMPLOYEE)
			        .build();
				  	if(emailExist.isPresent()){
				  		throw new UserAlreadyExistException("employee with " + emailExist.get().getEmail() + " is already exists ");
				  		
				  	}
				  	else
				  	{
					    userRepo.save(employee);
				  	}
				  	 var jwtToken = jwtService.generateToken(employee);
				     return AuthenticationResponse.builder()
				         .accessToken(jwtToken)
				         .build();
	}
	
	
}
