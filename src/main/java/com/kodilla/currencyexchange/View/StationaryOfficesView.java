package com.kodilla.currencyexchange.View;

import com.kodilla.currencyexchange.domain.StationaryOfficeDto;
import com.kodilla.currencyexchange.service.StationaryOfficeService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Route("stationary-offices")
@PageTitle("StationaryOffices")
public class StationaryOfficesView extends VerticalLayout {

    private final StationaryOfficeService stationaryOfficeService;

    private TextField phone = new TextField("Phone Number");
    private TextField address = new TextField("Address");

    private Button submitButton = new Button("Add Office");
    private Button cancel = new Button("Cancel");
    private Grid<StationaryOfficeDto> stationaryOfficeGrid = new Grid<>(StationaryOfficeDto.class);
    private IntegerField idFilter = new IntegerField("Filter by office Id");
    private Button refreshButton = new Button("Refresh");


    public StationaryOfficesView(StationaryOfficeService stationaryOfficeService) {
        this.stationaryOfficeService = stationaryOfficeService;

        setSizeFull();
        configureComponents();
        add(createFormLayout(), createButtonLayout(), stationaryOfficeGrid);
        updateList();
    }

    private void configureComponents() {
        stationaryOfficeGrid.setColumns("id", "phone", "address");
        stationaryOfficeGrid.setSizeFull();
        stationaryOfficeGrid.setWidthFull();

        idFilter.setPlaceholder("Enter office id...");
        idFilter.setValueChangeMode(ValueChangeMode.LAZY);
        idFilter.addValueChangeListener(e -> {
            Long id = e.getValue() != null ? e.getValue().longValue() : null;
            updateList(id);
        });

        submitButton.addClickListener(e -> {
            try {
                createOffice();
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
        });
        cancel.addClickListener(e -> UI.getCurrent().navigate("main"));
        refreshButton.addClickListener(e -> {
            Long id = idFilter.getValue() != null ? idFilter.getValue().longValue() : null;
            updateList(id);
        });
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(phone, address);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(submitButton, cancel, idFilter, refreshButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        return buttonLayout;
    }

    private void createOffice() throws UnsupportedEncodingException {
        UI currentUI = UI.getCurrent();

        String phoneNumber = phone.getValue();
        String localAddress = address.getValue();

        List<String> missingFields = new ArrayList<>();
        if (phoneNumber==null) missingFields.add("phone number");
        if (address.isEmpty()) missingFields.add("address");

        if (!missingFields.isEmpty()) {
            String missingFieldsMessage = String.join(", ", missingFields);
            currentUI.access(() ->
                    Notification.show("Missing required fields: " + missingFieldsMessage, 5000, Notification.Position.MIDDLE));
            return;
        }

        StationaryOfficeDto newOffice = StationaryOfficeDto.builder()
                .phone(phoneNumber)
                .address(localAddress)
                .build();

        stationaryOfficeService.createOffice(newOffice).subscribe(
                result -> currentUI.accessSynchronously(() -> {
                    Notification.show("Office added successfully", 3000, Notification.Position.MIDDLE);
                    phone.clear();
                    address.clear();
                    updateList();
                }),
                error -> currentUI.access(() ->
                        Notification.show("Error saving office: " + error.getMessage(), 3000, Notification.Position.MIDDLE))
        );
    }

    private void updateList() {
        updateList(null);
    }

    private void updateList(Long id) {
        UI ui = UI.getCurrent();
        if (id == null) {
            stationaryOfficeService.getAll()
                    .collectList()
                    .subscribe(offices -> ui.access(() -> stationaryOfficeGrid.setItems(offices)),
                            error -> ui.access(() -> Notification.show("Failed to load offices: " + error.getMessage(), 3000, Notification.Position.MIDDLE)));
        } else {
            stationaryOfficeService.getOfficeById(id)
                    .collectList()
                    .onErrorResume(WebClientResponseException.NotFound.class, e -> {
                        ui.access(() -> Notification.show("There is no office with given ID: " + id, 3000, Notification.Position.MIDDLE));
                        return Mono.just(Collections.emptyList());
                    })
                    .subscribe(offices -> {
                        if (!offices.isEmpty()) {
                            ui.access(() -> stationaryOfficeGrid.setItems(offices));
                        }
                    }, error -> ui.access(() -> Notification.show("Error retrieving office details: " + error.getMessage(), 3000, Notification.Position.MIDDLE)));
        }
    }
}
