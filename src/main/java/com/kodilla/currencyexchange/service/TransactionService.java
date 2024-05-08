package com.kodilla.currencyexchange.service;

import com.kodilla.currencyexchange.domain.TransactionDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionService {

    private final WebClient webClient;

    public TransactionService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<TransactionDto> getAll() {
        return webClient.get()
                .uri("/transactions")
                .retrieve()
                .bodyToFlux(TransactionDto.class)
                .onErrorResume(e -> Flux.empty());
    }

    public Flux<TransactionDto> getAllTransactionsByUserLogin(String userLogin) {
        return webClient.get()
                .uri("/transactions/userlogin/{userlogin}", userLogin)
                .retrieve()
                .bodyToFlux(TransactionDto.class)
                .onErrorResume(e -> Flux.empty());
    }

    public Mono<TransactionDto> createTransaction(TransactionDto transactionDto) {
        return webClient.post()
                .uri("/transactions")
                .bodyValue(transactionDto)
                .retrieve()
                .bodyToMono(TransactionDto.class);
    }

    public Mono<TransactionDto> getTransactionById(Long transactionId) {
        return webClient.get()
                .uri("/transactions/{transactionId}", transactionId)
                .retrieve()
                .bodyToMono(TransactionDto.class);
    }
}
