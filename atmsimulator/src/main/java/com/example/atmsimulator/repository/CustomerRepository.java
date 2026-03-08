package com.example.atmsimulator.repository;

import com.example.atmsimulator.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    // Login check
    Customer findByAccountNumberAndPin(Integer accountNumber, String pin);
}
