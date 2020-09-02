package com.example.atm.services;

import com.example.atm.domain.BalanceQuerying;
import com.example.atm.domain.Card;
import com.example.atm.domain.CashRequest;
import com.example.atm.dto.BalanceDto;
import com.example.atm.dto.CardDto;
import com.example.atm.exceptions.NotEnoughMoneyOnCard;
import com.example.atm.mappers.CardMapper;
import com.example.atm.repositories.CardRepository;
import com.example.atm.repositories.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
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

    public BalanceDto performCashRequest(String cardNumber, BigDecimal amount) {
        final Card card = cardRepository.getByNumberAndBlockedIsFalse(cardNumber);
        if (card.getBalance().compareTo(amount) < 0) {
            throw new NotEnoughMoneyOnCard();
        }
        card.setBalance(card.getBalance().subtract(amount));
        cardRepository.save(card);

        final CashRequest cashRequest = new CashRequest();
        cashRequest.setCard(card);
        cashRequest.setCreated(LocalDateTime.now());
        cashRequest.setAmount(amount);
        operationRepository.save(cashRequest);

        return mapper.cardBalanceToBalanceDto(card);
    }

}
