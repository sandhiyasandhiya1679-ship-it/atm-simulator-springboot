package com.example.atmsimulator.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @Column(name = "account_number")
    private Integer accountNumber;

    private String name;

    private String pin;

    private double balance = 0;

    public Customer() {}

    public Customer(Integer accountNumber, String name, String pin) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.pin = pin;
    }

    // Getters and Setters
    public Integer getAccountNumber() { return accountNumber; }
    public void setAccountNumber(Integer accountNumber) { this.accountNumber = accountNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
