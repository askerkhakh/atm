package com.example.atm;

import com.example.atm.domain.BalanceQuerying;
import com.example.atm.domain.Card;
import com.example.atm.domain.CashRequest;
import com.example.atm.domain.Operation;
import com.example.atm.dto.BalanceDto;
import com.example.atm.dto.CardDto;
import com.example.atm.repositories.CardRepository;
import com.example.atm.repositories.OperationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.jpa.hibernate.ddl-auto=create"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class AtmApplicationTests {

    public static final String TEST_CARD_NUMBER = "123";
    public static final String TEST_CARD_PIN = "1234";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void getExistingCardByNumber() {
        // given:
        saveTestCardInRepository();

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
        final Card card = buildDefaultValidCard();
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

    @Test
    void successfulLogin() {
        // given:
        saveTestCardInRepository();

        // when:
        final var headers = new HttpHeaders();
        headers.setBasicAuth(TEST_CARD_NUMBER, TEST_CARD_PIN);
        final ResponseEntity<Object> response = restTemplate.exchange("/login", HttpMethod.POST, new HttpEntity<>(null, headers), Object.class);

        // then:
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }

    @Test
    void loginWithIncorrectPassword() {
        // given:
        saveTestCardInRepository();

        // when:
        final var headers = new HttpHeaders();
        headers.setBasicAuth(TEST_CARD_NUMBER, "4321");
        final ResponseEntity<Object> response = restTemplate.exchange("/login", HttpMethod.POST, new HttpEntity<>(null, headers), Object.class);

        // then:
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.UNAUTHORIZED);
    }

    private void saveTestCardInRepository() {
        final Card card = buildDefaultValidCard();
        cardRepository.save(card);
    }

    @Test
    void getBalance() {
        // given:
        final Card card = buildDefaultValidCard();
        card.setBalance(BigDecimal.ONE);
        cardRepository.save(card);

        // when:
        final var response = restTemplate
                .withBasicAuth(TEST_CARD_NUMBER, TEST_CARD_PIN)
                .getForEntity("/balance/{number}", BalanceDto.class, Map.of("number", TEST_CARD_NUMBER));

        // then:
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAmount()).isEqualByComparingTo(BigDecimal.ONE);

        final List<Operation> operations = operationRepository.findAll();
        assertThat(operations).hasSize(1);
        final Operation operation = operations.get(0);
        assertThat(operation).isInstanceOf(BalanceQuerying.class);
        assertThat(operation.getCard()).isEqualTo(card);
    }

    private Card buildDefaultValidCard() {
        final Card card = new Card();
        card.setNumber(TEST_CARD_NUMBER);
        card.setPin("{noop}" + TEST_CARD_PIN);
        return card;
    }

    @Test
    void getCashWhenThereIsNotEnoughMoneyOnCard() {
        // given:
        final Card card = buildDefaultValidCard();
        card.setBalance(BigDecimal.ONE);
        cardRepository.save(card);

        // when:
        final var response = restTemplate
                .withBasicAuth(TEST_CARD_NUMBER, TEST_CARD_PIN)
                .postForEntity(
                        "/cash-request?card-number={cardNumber}&amount={amount}",
                        null,
                        BalanceDto.class,
                        Map.of("cardNumber", TEST_CARD_NUMBER, "amount", BigDecimal.TEN)
                );

        // then:
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getCashWhenThereIsEnoughMoneyOnCard() {
        // given:
        final Card card = buildDefaultValidCard();
        card.setBalance(BigDecimal.TEN);
        cardRepository.save(card);

        // when:
        final var response = restTemplate
                .withBasicAuth(TEST_CARD_NUMBER, TEST_CARD_PIN)
                .postForEntity(
                        "/cash-request?card-number={cardNumber}&amount={amount}",
                        null,
                        BalanceDto.class,
                        Map.of("cardNumber", TEST_CARD_NUMBER, "amount", BigDecimal.ONE)
                );

        // then:
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(9));
        final Card savedCard = cardRepository.getByNumberAndBlockedIsFalse(TEST_CARD_NUMBER);
        assertThat(savedCard.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(9));

        final List<Operation> operations = operationRepository.findAll();
        assertThat(operations).hasSize(1);
        final Operation operation = operations.get(0);
        assertThat(operation).isInstanceOf(CashRequest.class);
        assertThat(operation.getCard()).isEqualTo(card);
        assertThat(((CashRequest) operation).getAmount()).isEqualByComparingTo(BigDecimal.ONE);
    }

}
