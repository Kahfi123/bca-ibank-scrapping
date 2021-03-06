package com.brick.bca.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

public class AccountResponse implements Serializable {
    @JsonProperty("user_full_name")
    private String userFullName;
    @JsonProperty("user_balance")
    private Double userBalance;
    @JsonProperty("bank_account_number")
    private int bankAccountNumber;
    @JsonProperty("transactions")
    private ArrayList<Transaction> transactions;
}
