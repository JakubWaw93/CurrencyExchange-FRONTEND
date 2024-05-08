package com.kodilla.currencyexchange.service;

import com.kodilla.currencyexchange.domain.StationaryOfficeDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StationaryOfficeService {

    private final WebClient webClient;

    public StationaryOfficeService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<StationaryOfficeDto> getAll() {
        return webClient.get()
                .uri("/offices")
                .retrieve()
                .bodyToFlux(StationaryOfficeDto.class)
                .onErrorResume(e -> Flux.empty());
    }

    public Mono<StationaryOfficeDto> createOffice(StationaryOfficeDto stationaryOfficeDto) {
        return webClient.post()
                .uri("/offices")
                .bodyValue(stationaryOfficeDto)
                .retrieve()
                .bodyToMono(StationaryOfficeDto.class);
    }

    public Flux<StationaryOfficeDto> getOfficeById(Long officeId) {
        return webClient.get()
                .uri("/offices/{officeId}", officeId)
                .retrieve()
                .bodyToFlux(StationaryOfficeDto.class);
    }
}
