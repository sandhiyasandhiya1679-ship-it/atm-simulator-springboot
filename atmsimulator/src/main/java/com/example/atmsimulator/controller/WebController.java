package com.example.atmsimulator.controller;

import com.example.atmsimulator.model.Customer;
import com.example.atmsimulator.service.BankService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@SessionAttributes("currentUser")
public class WebController {

    @Autowired
    private BankService service;

    // ---------------- Welcome ----------------
    @GetMapping("/")
    public String welcome() { return "welcome"; }

    // ---------------- Signup ----------------
    @GetMapping("/signup")
    public String signupForm() { return "signup"; }

    @PostMapping("/signup")
    public String signup(@RequestParam Integer accountNumber,
                         @RequestParam String name,
                         @RequestParam String pin,
                         HttpSession session,
                         Model model) {
        try {
            service.signup(accountNumber, name, pin);
            Customer user = service.login(accountNumber, pin);
            session.setAttribute("currentUser", user);
            model.addAttribute("currentUser", user);
            return "signupSuccess";
        } catch (Exception e) {
            model.addAttribute("msg", e.getMessage());
            return "signup";
        }
    }

    // ---------------- Login ----------------
    @GetMapping("/login")
    public String loginForm() { return "login"; }

    @PostMapping("/login")
    public String login(@RequestParam Integer accountNumber,
                        @RequestParam String pin,
                        HttpSession session,
                        Model model) {
        Customer user = service.login(accountNumber, pin);
        if (user != null) {
            session.setAttribute("currentUser", user);
            model.addAttribute("currentUser", user);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid account number or PIN");
            return "login";
        }
    }

    // ---------------- Dashboard ----------------
    @GetMapping("/dashboard")
    public String dashboard(@SessionAttribute(value = "currentUser", required = false) Customer currentUser) {
        if (currentUser == null) return "redirect:/login";
        return "dashboard";
    }

    // ---------------- Logout ----------------
    @GetMapping("/logout")
    public String logout(HttpSession session, SessionStatus status) {
        session.invalidate();
        status.setComplete();
        return "redirect:/";
    }

    // ---------------- Deposit ----------------
    @GetMapping("/deposit")
    public String depositForm(@SessionAttribute(value = "currentUser", required = false) Customer currentUser) {
        if (currentUser == null) return "redirect:/login";
        return "deposit";
    }

    @PostMapping("/deposit")
    public String deposit(@SessionAttribute("currentUser") Customer currentUser,
                          @RequestParam double amount,
                          HttpSession session,
                          Model model) {
        service.deposit(currentUser.getAccountNumber(), amount);
        // update session object
        currentUser.setBalance(service.getBalance(currentUser.getAccountNumber()));
        model.addAttribute("msg", "Successfully deposited ₹" + amount);
        return "deposit";
    }

    // ---------------- Withdraw ----------------
    @GetMapping("/withdraw")
    public String withdrawForm(@SessionAttribute(value = "currentUser", required = false) Customer currentUser) {
        if (currentUser == null) return "redirect:/login";
        return "withdraw";
    }

    @PostMapping("/withdraw")
    public String withdraw(@SessionAttribute("currentUser") Customer currentUser,
                           @RequestParam double amount,
                           HttpSession session,
                           Model model) {
        boolean success = service.withdraw(currentUser.getAccountNumber(), amount);
        currentUser.setBalance(service.getBalance(currentUser.getAccountNumber())); // update session
        if (success) model.addAttribute("msg", "Successfully withdrew ₹" + amount);
        else model.addAttribute("error", "Insufficient balance!");
        return "withdraw";
    }

    // ---------------- Balance ----------------
    @GetMapping("/balance")
    public String balance(@SessionAttribute("currentUser") Customer currentUser, Model model) {
        double bal = service.getBalance(currentUser.getAccountNumber());
        currentUser.setBalance(bal); // update session
        model.addAttribute("balance", bal);
        return "balance";
    }

    // ---------------- Transfer ----------------
    @GetMapping("/transfer")
    public String transferForm(@SessionAttribute(value = "currentUser", required = false) Customer currentUser) {
        if (currentUser == null) return "redirect:/login";
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transfer(@SessionAttribute("currentUser") Customer currentUser,
                           @RequestParam int receiverAccount,
                           @RequestParam double amount,
                           HttpSession session,
                           Model model) {
        boolean success = service.transfer(currentUser.getAccountNumber(), receiverAccount, amount);
        currentUser.setBalance(service.getBalance(currentUser.getAccountNumber())); // update session
        if (success) model.addAttribute("msg", "Successfully transferred ₹" + amount + " to account " + receiverAccount);
        else model.addAttribute("error", "Transfer failed! Check balance or account number.");
        return "transfer";
    }

    // ---------------- Change PIN ----------------
    @GetMapping("/changePin")
    public String changePinForm(@SessionAttribute(value = "currentUser", required = false) Customer currentUser) {
        if (currentUser == null) return "redirect:/login";
        return "changePin";
    }

    @PostMapping("/changePin")
    public String changePin(@SessionAttribute("currentUser") Customer currentUser,
                            @RequestParam String newPin,
                            HttpSession session,
                            Model model) {
        service.changePin(currentUser.getAccountNumber(), newPin);
        currentUser.setPin(newPin); // update session
        model.addAttribute("msg", "PIN changed successfully!");
        return "changePin";
    }

    // ---------------- All Customers ----------------
    @GetMapping("/allCustomers")
    public String allCustomers(@SessionAttribute(value = "currentUser", required = false) Customer currentUser,
                               Model model) {
        if (currentUser == null) {
            return "redirect:/login"; // redirect if not logged in
        }

        model.addAttribute("customers", service.getAllCustomers()); // send all customers from DB
        return "allCustomers"; // render allCustomers.html
    }
    @GetMapping("/deleteCustomer/{accountNumber}")
    public String deleteCustomer(@PathVariable Integer accountNumber,
                                 @SessionAttribute(value = "currentUser", required = false) Customer currentUser,
                                 Model model) {
        if (currentUser == null) return "redirect:/login";

        try {
            service.deleteCustomer(accountNumber);
            model.addAttribute("msg", "Customer deleted successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        // reload all customers page
        model.addAttribute("customers", service.getAllCustomers());
        return "allCustomers";
    }

}
