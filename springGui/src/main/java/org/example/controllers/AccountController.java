package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.services.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    @GetMapping("/account/{id}")
    public String accountPage(@PathVariable int id, Model model) {
        model.addAttribute("account", accountService.getAccountById(id));
        return "account";
    }
    @GetMapping("/account")
    public String accountPage(Model model) {
        return "account";
    }

    @GetMapping("/ai")
    public String aiPage(Model model) {
        return "ai";
    }

    @GetMapping("/data")
    public String dataPage(Model model) {
        return "data-general";
    }
}
