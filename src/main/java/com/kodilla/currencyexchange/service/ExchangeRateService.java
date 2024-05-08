package com.kodilla.currencyexchange.service;

import com.kodilla.currencyexchange.domain.ExchangeRateDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
 public class ExchangeRateService {

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);

     public ExchangeRateService(WebClient webClient) {
         this.webClient = webClient;
     }

     public Flux<ExchangeRateDto> getAllExchangeRates() {
         return this.webClient.get()
                 .uri("/exchangerates")
                 .retrieve()
                 .bodyToFlux(ExchangeRateDto.class);
     }

     public Mono<ExchangeRateDto> getExchangeRateByBaseAndTargetCurrencyCodes(String baseCode, String targetCode) {
         String url = String.format("/exchangerates/codes/"+baseCode+"/"+targetCode);
         return webClient.get()
                 .uri(url)
                 .retrieve()
                 .bodyToMono(ExchangeRateDto.class)
                 .onErrorResume( e -> {
                     logger.error("Failed to fetch exchange rates for base {} and target {}: {}", baseCode, targetCode, e.getMessage());
                     return Mono.empty();
                 });
     }

     public Flux<ExchangeRateDto> getExchangeRatesByBaseCurrencyCode(String baseCode) {
         String url = String.format("/exchangerates/code/base/"+baseCode);
         return webClient.get()
                 .uri(url)
                 .retrieve()
                 .bodyToFlux(ExchangeRateDto.class)
                 .onErrorResume( e -> {
                     logger.error("Failed to fetch exchange rates for base {}: {}", baseCode, e.getMessage());
                     return Flux.empty();
                 });
     }

     public Flux<ExchangeRateDto> getExchangeRatesByTargetCurrencyCode(String targetCode) {
         String url = String.format("/exchangerates/code/target/"+targetCode);
         return webClient.get()
                 .uri(url)
                 .retrieve()
                 .bodyToFlux(ExchangeRateDto.class)
                 .onErrorResume( e -> {
                     logger.error("Failed to fetch exchange rates target {}: {}", targetCode, e.getMessage());
                     return Flux.empty();
                 });
     }
}