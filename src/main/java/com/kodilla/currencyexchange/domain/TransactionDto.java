package com.kodilla.currencyexchange.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionDto {

    private Long id;
    private Long userId;
    private Long boughtCurrencyId;
    private Long soldCurrencyId;
    private Long exchangeRateId;
    private BigDecimal amountBoughtCurrency;
    private String status;
    private LocalDateTime transactionDate;

}
