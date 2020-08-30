package com.example.atm.mappers;

import com.example.atm.domain.Card;
import com.example.atm.dto.CardDto;
import org.mapstruct.Mapper;

@Mapper
public interface CardMapper {

    CardDto cardToCardDto(Card card);

}
