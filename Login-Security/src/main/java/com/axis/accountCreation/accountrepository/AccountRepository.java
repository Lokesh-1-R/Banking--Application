package com.axis.accountCreation.accountrepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.axis.accountCreation.model.Account;
import com.axis.accountCreation.model.AccountStatus;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findAccountByUserId(Integer userId);
    Optional<Account> findAccountByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
}
    