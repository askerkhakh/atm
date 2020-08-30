package com.example.atm.repositories;

import com.example.atm.domain.Card;
import com.example.atm.dto.CardDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Card getByNumber(String number);

}
