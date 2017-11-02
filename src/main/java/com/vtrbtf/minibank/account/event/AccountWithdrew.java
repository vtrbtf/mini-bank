package com.vtrbtf.minibank.account.event;

import java.math.BigDecimal;

public class AccountWithdrew extends AccountTransactionEvent {
    public AccountWithdrew(String id, String name, BigDecimal value) {
        super(id, name, value);
    }
}