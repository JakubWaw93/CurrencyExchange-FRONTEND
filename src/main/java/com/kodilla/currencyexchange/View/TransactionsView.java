package com.kodilla.currencyexchange.View;

import com.kodilla.currencyexchange.domain.TransactionDto;
import com.kodilla.currencyexchange.service.ExchangeRateService;
import com.kodilla.currencyexchange.service.TransactionService;
import com.kodilla.currencyexchange.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Route("transactions")
@PageTitle("Transactions")
public class TransactionsView extends VerticalLayout {

    private final UserService userService;
    private final TransactionService transactionService;
    private final ExchangeRateService exchangeRateService;

    private TextField emailAddress = new TextField("Email Address");
    private TextField boughtCurrencyField = new TextField("Bought Currency Code");
    private TextField soldCurrencyField = new TextField("Sold Currency Code");
    private BigDecimalField amountOfBoughtCurrency = new BigDecimalField("Amount of currency to buy");
    private Button submitButton = new Button("Submit Transaction");
    private Button cancelButton = new Button("Cancel Transaction");
    private Grid<TransactionDto> transactionsGrid = new Grid<>(TransactionDto.class);
    private TextField userLoginFilter = new TextField("Filter by User Login");
    private Button refreshButton = new Button("Refresh");


    public TransactionsView(UserService userService, TransactionService transactionService, ExchangeRateService exchangeRateService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.exchangeRateService = exchangeRateService;

        setSizeFull();
        configureComponents();
        add(createFormLayout(), createButtonLayout(), transactionsGrid);
        updateList();
    }

    private void configureComponents() {
        transactionsGrid.setColumns("id", "userId", "boughtCurrencyId", "soldCurrencyId", "exchangeRateId", "amountBoughtCurrency", "status", "transactionDate");
        transactionsGrid.setSizeFull();
        transactionsGrid.setWidthFull();

        userLoginFilter.setPlaceholder("Enter user login...");
        userLoginFilter.setValueChangeMode(ValueChangeMode.LAZY);
        userLoginFilter.addValueChangeListener(e -> updateList(e.getValue()));

        submitButton.addClickListener(e -> {
            try {
                createTransaction();
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
        });
        cancelButton.addClickListener(e -> UI.getCurrent().navigate("main"));
        refreshButton.addClickListener(e -> updateList(userLoginFilter.getValue()));
    }


    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(emailAddress, boughtCurrencyField, soldCurrencyField, amountOfBoughtCurrency);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(submitButton, cancelButton, userLoginFilter, refreshButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        return buttonLayout;
    }

    private void createTransaction() throws UnsupportedEncodingException {
        UI currentUI = UI.getCurrent();

        String email = emailAddress.getValue();
        String boughtCurrencyCode = boughtCurrencyField.getValue();
        String soldCurrencyCode = soldCurrencyField.getValue();
        BigDecimal amount = amountOfBoughtCurrency.getValue();

        List<String> missingFields = new ArrayList<>();
        if (email.isEmpty()) missingFields.add("email");
        if (boughtCurrencyCode.isEmpty()) missingFields.add("bought currency code");
        if (soldCurrencyCode.isEmpty()) missingFields.add("sold currency code");
        if (amount == null) missingFields.add("amount of bought currency");

        if (!missingFields.isEmpty()) {
            String missingFieldsMessage = String.join(", ", missingFields);
            currentUI.access(() ->
                    Notification.show("Missing required fields: " + missingFieldsMessage, 5000, Notification.Position.MIDDLE));
            return;
        }

        userService.getUserByEmail(email)
                .flatMap(user ->
                        exchangeRateService.getExchangeRateByBaseAndTargetCurrencyCodes(boughtCurrencyCode, soldCurrencyCode)
                                .switchIfEmpty(Mono.error(new Exception("Invalid currency codes: No exchange rate found")))
                                .map(rate -> TransactionDto.builder()
                                        .userId(user.getId())
                                        .boughtCurrencyId(rate.getTargetCurrencyId())
                                        .soldCurrencyId(rate.getBaseCurrencyId())
                                        .exchangeRateId(rate.getId())
                                        .amountBoughtCurrency(amount)
                                        .status("IN_PROGRESS")
                                        .transactionDate(LocalDateTime.now())
                                        .build())
                ).subscribe(
                        transactionDto -> handleTransactionCreation(transactionDto, currentUI),
                        error -> currentUI.access(() ->
                                Notification.show("Failed to create transaction: " + error.getMessage(), 3000, Notification.Position.MIDDLE))
                );

    }

    private void handleTransactionCreation(TransactionDto transactionDto, UI ui) {
        transactionService.createTransaction(transactionDto).subscribe(
                result -> {
                    ui.access(() -> {
                        Notification.show("Transaction created successfully", 3000, Notification.Position.MIDDLE);
                        emailAddress.clear();
                        boughtCurrencyField.clear();
                        soldCurrencyField.clear();
                        amountOfBoughtCurrency.clear();
                    });
                },
                error -> {
                    ui.access(() -> {
                        Notification.show("Error saving transaction: " + error.getMessage(), 3000, Notification.Position.MIDDLE);
                    });
                }
        );
    }

    private void updateList() {
        updateList("");
    }

    private void updateList(String filter) {
        UI ui = UI.getCurrent();
        if (filter.isEmpty()) {
            transactionService.getAll()
                    .collectList()
                    .subscribe(transactions -> {
                        ui.access(() -> transactionsGrid.setItems(transactions));
                    });
        } else {
            transactionService.getAllTransactionsByUserLogin(filter)
                    .collectList()
                    .subscribe(transactions -> {
                        ui.access(() -> transactionsGrid.setItems(transactions));
                    });
        }
    }
}
