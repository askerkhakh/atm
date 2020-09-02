package com.example.atm.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class CashRequest extends Operation {

    BigDecimal amount;

}
