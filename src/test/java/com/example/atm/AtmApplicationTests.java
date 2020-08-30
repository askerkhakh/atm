package com.example.atm;

import com.example.atm.domain.Card;
import com.example.atm.dto.CardDto;
import com.example.atm.repositories.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.jpa.hibernate.ddl-auto=create"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AtmApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CardRepository cardRepository;
    public static final String TEST_CARD_NUMBER = "123";

    @Test
    void contextLoads() {
    }

    @Test
    void getExistingCardByNumber() {
        // given:
        final Card card = new Card();
        card.setNumber(TEST_CARD_NUMBER);
        cardRepository.save(card);

        // when:
        final ResponseEntity<CardDto> response = restTemplate.getForEntity(
                "/cards/{card-number}",
                CardDto.class,
                Map.of("card-number", TEST_CARD_NUMBER)
        );

        // then:
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getNumber()).isEqualTo(TEST_CARD_NUMBER);
    }

    @Test
    void getCardByNumberWhichDoesNotExist() {
        // when:
        final ResponseEntity<CardDto> response = restTemplate.getForEntity(
                "/cards/{card-number}",
                CardDto.class,
                Map.of("card-number", "someRandomNumber")
        );

        // then:
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getBlockedCardShouldReturn404() {
        // given:
        final Card card = new Card();
        card.setNumber(TEST_CARD_NUMBER);
        card.setBlocked(true);
        cardRepository.save(card);

        // when:
        final ResponseEntity<CardDto> response = restTemplate.getForEntity(
                "/cards/{card-number}",
                CardDto.class,
                Map.of("card-number", TEST_CARD_NUMBER)
        );

        // then:
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    }

}
