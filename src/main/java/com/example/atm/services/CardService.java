package com.example.atm.services;

import com.example.atm.dto.CardDto;
import com.example.atm.mappers.CardMapper;
import com.example.atm.repositories.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper mapper;

    public CardDto getCardByNumber(String number) {
        return mapper.cardToCardDto(cardRepository.getByNumber(number));
    }

}
