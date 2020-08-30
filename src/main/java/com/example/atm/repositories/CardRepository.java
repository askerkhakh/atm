package com.example.atm.repositories;

import com.example.atm.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByNumber(String number);

    default Card getByNumber(String number) {
        return findByNumber(number).orElseThrow(EntityNotFoundException::new);
    }

}
