package com.example.atmsimulator.service;

import com.example.atmsimulator.model.Customer;
import com.example.atmsimulator.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankService {

    @Autowired
    private CustomerRepository repo;

    // ---------------- Signup ----------------
    public void signup(Integer accountNumber, String name, String pin) {
        if (repo.existsById(accountNumber)) {
            throw new RuntimeException("Account number already exists!");
        }
        Customer c = new Customer(accountNumber, name, pin);
        repo.save(c);
    }

    // ---------------- Login ----------------
    public Customer login(Integer accountNumber, String pin) {
        return repo.findByAccountNumberAndPin(accountNumber, pin);
    }

    public boolean existsByAccountNumber(Integer accountNumber) {
        return repo.existsById(accountNumber);
    }

    // ---------------- Deposit ----------------
    public void deposit(Integer accountNumber, double amount) {
        Customer c = repo.findById(accountNumber).orElseThrow(() -> new RuntimeException("Account not found"));
        c.setBalance(c.getBalance() + amount);
        repo.save(c);
    }

    // ---------------- Withdraw ----------------
    public boolean withdraw(Integer accountNumber, double amount) {
        Customer c = repo.findById(accountNumber).orElseThrow(() -> new RuntimeException("Account not found"));
        if (c.getBalance() >= amount) {
            c.setBalance(c.getBalance() - amount);
            repo.save(c);
            return true;
        }
        return false;
    }

    // ---------------- Transfer ----------------
    public boolean transfer(Integer senderAccount, Integer receiverAccount, double amount) {
        Customer sender = repo.findById(senderAccount).orElseThrow(() -> new RuntimeException("Sender not found"));
        Optional<Customer> receiverOpt = repo.findById(receiverAccount);
        if (sender.getBalance() >= amount && receiverOpt.isPresent()) {
            Customer receiver = receiverOpt.get();
            sender.setBalance(sender.getBalance() - amount);
            receiver.setBalance(receiver.getBalance() + amount);
            repo.save(sender);
            repo.save(receiver);
            return true;
        }
        return false;
    }

    // ---------------- Change PIN ----------------
    public void changePin(Integer accountNumber, String newPin) {
        Customer c = repo.findById(accountNumber).orElseThrow(() -> new RuntimeException("Account not found"));
        c.setPin(newPin);
        repo.save(c);
    }

    // ---------------- Get Balance ----------------
    public double getBalance(Integer accountNumber) {
        Customer c = repo.findById(accountNumber).orElseThrow(() -> new RuntimeException("Account not found"));
        return c.getBalance();
    }

    // ---------------- Get All Customers ----------------
    public List<Customer> getAllCustomers() {
        return repo.findAll();
    }
    public void deleteCustomer(Integer accountNumber) {
        if(repo.existsById(accountNumber)) {
            repo.deleteById(accountNumber);
        } else {
            throw new RuntimeException("Customer with account number " + accountNumber + " not found!");
        }
    }

}


