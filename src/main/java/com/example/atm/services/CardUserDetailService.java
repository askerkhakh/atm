package com.example.atm.services;

import com.example.atm.domain.Card;
import com.example.atm.repositories.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardUserDetailService implements UserDetailsService {

    private final CardRepository cardRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<Card> cardOptional = cardRepository.findByNumberAndBlockedIsFalse(username);
        final Card card = cardOptional.orElseThrow(() -> new UsernameNotFoundException(username));
        return new User(card.getNumber(), card.getPin(), true, true, true, !card.isBlocked(), List.of());
    }

}
