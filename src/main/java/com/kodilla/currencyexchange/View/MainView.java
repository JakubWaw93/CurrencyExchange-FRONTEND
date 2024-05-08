package com.kodilla.currencyexchange.View;

import com.kodilla.currencyexchange.domain.CurrencyDto;
import com.kodilla.currencyexchange.domain.ExchangeRateDto;
import com.kodilla.currencyexchange.service.CurrencyService;
import com.kodilla.currencyexchange.service.ExchangeRateService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Route("main")
@PageTitle("Main")
public class MainView extends VerticalLayout {

    private final CurrencyService currencyService;
    private final ExchangeRateService exchangeRateService;

    private Grid<CurrencyDto> currencyGrid = new Grid<>(CurrencyDto.class);
    private TextField filterTextField = new TextField("Filter by Code");
    private Button filterButton = new Button("Filter");
    private Button addCurrencyButton = new Button("Add Currency");
    private Button logoutButton = new Button("Logout");
    private Button transactionsButton = new Button("Transactions");
    private Button stationaryOfficesButton = new Button("Find us locally");

    private TextField baseCurrencyField = new TextField("Base Currency Code");
    private TextField targetCurrencyField = new TextField("Target Currency Code");
    private Button searchExchangeRateButton = new Button("Search Exchange Rates");
    private Grid<ExchangeRateDto> exchangeRateGrid = new Grid<>(ExchangeRateDto.class);

    public MainView(CurrencyService currencyService, ExchangeRateService exchangeRateService) {
        this.currencyService = currencyService;
        this.exchangeRateService = exchangeRateService;
        configureComponents();
        addComponents();
        updateCurrencyList();
    }

    private void configureComponents() {
        currencyGrid.setColumns("code", "name");
        currencyGrid.setSizeFull();

        filterTextField.setPlaceholder("Enter currency code...");
        filterTextField.setValueChangeMode(ValueChangeMode.EAGER);

        filterButton.addClickListener(e -> {
            updateCurrencyList(filterTextField.getValue());
        });
        addCurrencyButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("add-currency"));
        });
        logoutButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("login"));
        });
        transactionsButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("transactions"));
        });
        stationaryOfficesButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("stationary-offices"));
        });

        baseCurrencyField.setPlaceholder("Enter base currency code...");
        targetCurrencyField.setPlaceholder("Enter target currency code...");
        searchExchangeRateButton.addClickListener(e -> updateExchangeRateList());
        exchangeRateGrid.setColumns("baseCurrencyCode", "targetCurrencyCode", "rate", "lastUpdateTime");
        exchangeRateGrid.setSizeFull();
    }

    private void addComponents() {
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidthFull();
        topBar.add(filterTextField, filterButton, addCurrencyButton);
        topBar.add(transactionsButton, stationaryOfficesButton, logoutButton);
        topBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        topBar.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        add(topBar, currencyGrid, baseCurrencyField, targetCurrencyField, searchExchangeRateButton, exchangeRateGrid);
        setSizeFull();
    }

    private void updateCurrencyList() {
        currencyService.getAllCurrencies()
                .collectList()
                .subscribe(currencies -> {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        currencyGrid.setItems(currencies);
                        currencyGrid.getDataProvider().refreshAll();
                    }));
                });
    }

    private void updateCurrencyList(String code) {
        if (code.isEmpty()) {
            updateCurrencyList();
        } else {
            currencyService.getCurrencyByCode(code)
                    .subscribe(currency -> {
                        getUI().ifPresent(ui -> ui.access(() -> {
                            if (currency != null) {
                                currencyGrid.setItems(currency);
                            } else {
                                Notification.show("No currency found with code: " + code, 5000, Notification.Position.MIDDLE);
                                currencyGrid.setItems();
                            }
                        }));
                    }, error -> {
                        getUI().ifPresent(ui -> ui.access(() ->
                                Notification.show("Error retrieving currency: " + error.getMessage(), 5000, Notification.Position.MIDDLE)
                        ));
                    });
        }
    }

    private void updateExchangeRateList() {
        String baseCode = baseCurrencyField.getValue().trim();
        String targetCode = targetCurrencyField.getValue().trim();

        Mono<List<ExchangeRateDto>> ratesMono;
        if (!baseCode.isEmpty() && !targetCode.isEmpty()) {
            ratesMono = exchangeRateService.getExchangeRateByBaseAndTargetCurrencyCodes(baseCode, targetCode).map(Collections::singletonList);
        } else if (!baseCode.isEmpty()) {
            ratesMono = exchangeRateService.getExchangeRatesByBaseCurrencyCode(baseCode).collectList();
        } else if (!targetCode.isEmpty()) {
            ratesMono = exchangeRateService.getExchangeRatesByTargetCurrencyCode(targetCode).collectList();
        } else {
            ratesMono = exchangeRateService.getAllExchangeRates().collectList();
        }

        ratesMono.subscribe(rates -> {
            getUI().ifPresent(ui -> ui.access(() -> {
                if (!rates.isEmpty()) {
                    exchangeRateGrid.setItems(rates);
                } else {
                    Notification.show("No exchange rates found for the provided criteria", 5000, Notification.Position.MIDDLE);
                    exchangeRateGrid.setItems();
                }
            }));
        }, error -> {
            getUI().ifPresent(ui -> ui.access(() ->
                    Notification.show("Error retrieving exchange rates: " + error.getMessage(), 5000, Notification.Position.MIDDLE)
            ));
        });
    }

}
