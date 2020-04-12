package com.axlabs.neo.tutorial.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BalanceResponse {

    @JsonProperty
    private String address;

    @JsonProperty
    private int neoBalance;

    @JsonProperty
    private double gasBalance;

    @JsonProperty
    private int totalWalletNeoBalance;

    @JsonProperty
    private double totalWalletGasBalance;

    public BalanceResponse(String address,
                           int neoBalance,
                           double gasBalance,
                           int totalWalletNeoBalance,
                           double totalWalletGasBalance) {
        this.address = address;
        this.neoBalance = neoBalance;
        this.gasBalance = gasBalance;
        this.totalWalletNeoBalance = totalWalletNeoBalance;
        this.totalWalletGasBalance = totalWalletGasBalance;
    }

    public String getAddress() {
        return this.address;
    }

    public int getNeoBalance() {
        return this.neoBalance;
    }

    public double getGasBalance() {
        return this.gasBalance;
    }

    public int getTotalWalletNeoBalance() {
        return this.totalWalletNeoBalance;
    }

    public double getTotalWalletGasBalance() {
        return this.totalWalletGasBalance;
    }

}
