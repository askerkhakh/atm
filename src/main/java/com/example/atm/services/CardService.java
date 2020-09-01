package com.example.atm.services;

import com.example.atm.domain.BalanceQuerying;
import com.example.atm.domain.Card;
import com.example.atm.dto.BalanceDto;
import com.example.atm.dto.CardDto;
import com.example.atm.mappers.CardMapper;
import com.example.atm.repositories.CardRepository;
import com.example.atm.repositories.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final OperationRepository operationRepository;
    private final CardMapper mapper;

    public CardDto getCardByNumber(String number) {
        return mapper.cardToCardDto(cardRepository.getByNumberAndBlockedIsFalse(number));
    }

    public BalanceDto getCardBalanceByNumber(String cardNumber) {
        final Card card = cardRepository.getByNumberAndBlockedIsFalse(cardNumber);

        final BalanceQuerying balanceQuerying = new BalanceQuerying();
        balanceQuerying.setCard(card);
        balanceQuerying.setCreated(LocalDateTime.now());
        operationRepository.save(balanceQuerying);

        return mapper.cardBalanceToBalanceDto(card);
    }

}
