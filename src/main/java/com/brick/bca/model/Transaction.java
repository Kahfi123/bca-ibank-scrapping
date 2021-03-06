package com.brick.bca.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    @JsonProperty("date")
    private Date date;
    @JsonProperty("description")
    private String description;
    @JsonProperty("branch")
    private String branch;
    @JsonProperty("amount")
    private Double amount;
    @JsonProperty("type")
    private String type;
    @JsonProperty("balance")
    private Double balance;

}
