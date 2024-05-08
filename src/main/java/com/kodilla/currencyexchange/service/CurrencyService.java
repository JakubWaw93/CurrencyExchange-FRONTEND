package com.kodilla.currencyexchange.service;

import com.kodilla.currencyexchange.domain.CurrencyDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class CurrencyService {

    private final WebClient webClient;


    public CurrencyService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<CurrencyDto> getAllCurrencies() {
        return this.webClient.get()
                .uri("/currencies")
                .retrieve()
                .bodyToFlux(CurrencyDto.class);
    }

    public Mono<CurrencyDto> getCurrencyByCode(String code) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/currencies/code/{code}").build(code))
                .retrieve()
                .bodyToMono(CurrencyDto.class);
    }

    public Mono<CurrencyDto> addCurrency(CurrencyDto newCurrency) {
        return this.webClient.post()
                .uri("/currencies")
                .body(BodyInserters.fromValue(newCurrency))
                .retrieve()
                .bodyToMono(CurrencyDto.class);
    }
}


