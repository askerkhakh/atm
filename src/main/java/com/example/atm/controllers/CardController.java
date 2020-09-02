package com.example.atm.controllers;

import com.example.atm.dto.BalanceDto;
import com.example.atm.dto.CardDto;
import com.example.atm.services.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/cards/{number}")
    CardDto cardByNumber(@PathVariable String number) {
        return cardService.getCardByNumber(number);
    }

    @GetMapping("/balance/{cardNumber}")
    BalanceDto getBalance(@PathVariable String cardNumber) {
        return cardService.getCardBalanceByNumber(cardNumber);
    }

    @PostMapping("cash-request")
    BalanceDto performCashRequest(@RequestParam("card-number") String cardNumber, @RequestParam BigDecimal amount) {
        return cardService.performCashRequest(cardNumber, amount);
    }

}
