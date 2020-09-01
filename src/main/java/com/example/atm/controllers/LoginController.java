package com.example.atm.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class LoginController {

    @PostMapping("/login")
    Principal login(Principal user) {
        return user;
    }

}
