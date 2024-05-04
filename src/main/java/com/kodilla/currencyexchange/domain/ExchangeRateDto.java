package com.kodilla.currencyexchange.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ExchangeRateDto {

    private Long id;
    private Long baseCurrencyId;
    private Long targetCurrencyId;
    @Builder.Default
    private List<Long> transactionsIds = new ArrayList<>();
    private BigDecimal rate;
    private LocalDateTime lastUpdateTime;

    private String baseCurrencyCode;
    private String targetCurrencyCode;

    public ExchangeRateDto(Long id, Long baseCurrencyId, Long targetCurrencyId,
                           List<Long> transactionsIds, BigDecimal rate, LocalDateTime lastUpdateTime,
                           String baseCurrencyCode, String targetCurrencyCode) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.transactionsIds = transactionsIds;
        this.rate = rate;
        this.lastUpdateTime = lastUpdateTime;
        this.baseCurrencyCode = baseCurrencyCode;
        this.targetCurrencyCode = targetCurrencyCode;
    }

}
