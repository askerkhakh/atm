package com.example.atm.mappers;

import com.example.atm.domain.Card;
import com.example.atm.dto.BalanceDto;
import com.example.atm.dto.CardDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CardMapper {

    CardDto cardToCardDto(Card card);

    @Mapping(source = "balance", target = "amount")
    BalanceDto cardBalanceToBalanceDto(Card card);

}
