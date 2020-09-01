package com.example.atm.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Card {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    long id;

    @Column(unique = true, nullable = false)
    String number;

    @Column(nullable = false)
    String pin;

    boolean blocked = false;

    BigDecimal balance = BigDecimal.valueOf(0);
}
