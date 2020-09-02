package com.example.atm.services;

import com.example.atm.domain.Card;
import com.example.atm.repositories.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    public static final int MAX_LOGIN_ATTEMPT = 4;
    private final CardRepository cardRepository;

    public void loginSucceeded(AuthenticationSuccessEvent event) {
        final User principal = (User) event.getAuthentication().getPrincipal();
        final String cardNumber = principal.getUsername();
        final Card card = cardRepository.getByNumberAndBlockedIsFalse(cardNumber);
        card.setLoginAttemptCount(0);
        cardRepository.save(card);
    }

    public void loginFailed(AuthenticationFailureBadCredentialsEvent event) {
        final String cardNumber = (String) event.getAuthentication().getPrincipal();
        final Optional<Card> cardOptional = cardRepository.findByNumberAndBlockedIsFalse(cardNumber);
        if (cardOptional.isPresent()) {
            final Card card = cardOptional.get();
            card.setLoginAttemptCount(card.getLoginAttemptCount() + 1);
            if (card.getLoginAttemptCount() >= MAX_LOGIN_ATTEMPT) {
                card.setBlocked(true);
            }
            cardRepository.save(card);
        }
    }

}
