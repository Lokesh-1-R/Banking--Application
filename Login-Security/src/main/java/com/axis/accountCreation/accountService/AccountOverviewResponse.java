package com.axis.accountCreation.accountService;


public record AccountOverviewResponse( double accountBalance,
        String accountNumber,
        String AccountType,
        String accountStatus
		) {

}
	