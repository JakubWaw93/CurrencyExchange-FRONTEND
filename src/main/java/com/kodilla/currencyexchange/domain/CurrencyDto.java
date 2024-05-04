package com.kodilla.currencyexchange.domain;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CurrencyDto {

    private Long id;
    private String code;
    private String name;
    @Builder.Default
    private List<Long> baseExchangeRatesIds = new ArrayList<>();
    @Builder.Default
    private List<Long> targetExchangeRatesIds = new ArrayList<>();
    @Builder.Default
    private List<Long> boughtInTransactionsIds = new ArrayList<>();
    @Builder.Default
    private List<Long> soldInTransactionsIds = new ArrayList<>();
    @Builder.Default
    private boolean active = true;
    private boolean crypto;

    public CurrencyDto(Long id, String code, String name, List<Long> baseExchangeRatesIds, List<Long> targetExchangeRatesIds, List<Long> boughtInTransactionsIds, List<Long> soldInTransactionsIds, boolean active, boolean crypto) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.baseExchangeRatesIds = new ArrayList<>();;
        this.targetExchangeRatesIds = new ArrayList<>();;
        this.boughtInTransactionsIds = new ArrayList<>();;
        this.soldInTransactionsIds = new ArrayList<>();;
        this.active = true;
        this.crypto = crypto;
    }
}
