package com.example.atm.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BalanceDto {
    BigDecimal amount;
}
