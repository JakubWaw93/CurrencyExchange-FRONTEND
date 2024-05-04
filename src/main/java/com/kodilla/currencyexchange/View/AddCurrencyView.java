package com.kodilla.currencyexchange.View;

import com.kodilla.currencyexchange.domain.CurrencyDto;
import com.kodilla.currencyexchange.service.CurrencyService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@Route("add-currency")
@PageTitle("Add Currency")
public class AddCurrencyView extends VerticalLayout {

    private final CurrencyService currencyService;
    private TextField code = new TextField("Currency Code");
    private TextField name = new TextField("Currency Name");
    private Checkbox crypto = new Checkbox("Is Crypto?");
    private Button saveButton = new Button("Save");

    public AddCurrencyView(CurrencyService currencyService) {
        this.currencyService = currencyService;
        setSizeFull();
        FormLayout formLayout = new FormLayout();

        code.setPlaceholder("Enter currency code");
        name.setPlaceholder("Enter currency name");

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> cancel());

        saveButton.addClickListener(e -> saveCurrency());
        saveButton.setEnabled(false);

        code.addValueChangeListener(e -> updateButtonState());
        name.addValueChangeListener(e -> updateButtonState());

        formLayout.add(code, name, crypto, saveButton, cancelButton);
        add(formLayout);
        setHorizontalComponentAlignment(Alignment.CENTER, formLayout);
    }

    private void saveCurrency() {

        if (code.isEmpty() || name.isEmpty()) {
            Notification.show("Currency code and name must not be empty.", 5000, Notification.Position.MIDDLE);
            return;
        }

        try {
            CurrencyDto newCurrency = CurrencyDto.builder()
                    .code(code.getValue())
                    .name(name.getValue())
                    .crypto(crypto.getValue())
                    .build();

            currencyService.addCurrency(newCurrency).subscribe(
                    currency -> {
                        UI currentUI = UI.getCurrent();
                        if (currentUI != null) {
                            currentUI.access(() -> {
                                Notification.show("Currency added successfully");
                                currentUI.navigate("main");
                            });
                        }
                    },
                    error -> {
                        UI currentUI = UI.getCurrent();
                        if (currentUI != null) {
                            currentUI.access(() -> Notification.show("Failed to add currency: " + error.getMessage()));
                        }
                    }
            );
        } catch (Exception e) {
            Notification.show("Error creating currency: " + e.getMessage());
        }
    }

    private void cancel() {
        getUI().ifPresent(ui -> ui.navigate("main"));
    }

    private void updateButtonState() {
        boolean enabled = !code.isEmpty() && !name.isEmpty();
        saveButton.setEnabled(enabled);
    }
}
