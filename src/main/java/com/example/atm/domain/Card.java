package com.example.atm.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Card {

    @Id
    @GeneratedValue
    long id;

    @Column(unique = true)
    String number;

    boolean blocked = false;
}
