package com.axis.accountCreation.model;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.axis.user.User;


@Entity
@Table(name = "accounts")
@Data
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @SequenceGenerator(
            name = "account_id_sequence",
            sequenceName = "account_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "account_id_sequence"
    )
    private Integer Id;
    
    
    private double accountBalance;
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    private String accountNumber;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
 
    private int userId;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/yyyy HH:mm:ss");


	public Account() {
		super();
		// TODO Auto-generated constructor stub
	}
  
    public Account(int id,int userId, double accountBalance, AccountStatus accountStatus, String accountNumber, AccountType accountType) {
       this.Id=id;
    	this.accountBalance = accountBalance;
        this.accountStatus = accountStatus;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.createdAt = LocalDateTime.parse(
                DATE_TIME_FORMATTER.format(LocalDateTime.now()),
                DATE_TIME_FORMATTER);
        this.updatedAt = createdAt;
        this.userId = userId;
    }

}