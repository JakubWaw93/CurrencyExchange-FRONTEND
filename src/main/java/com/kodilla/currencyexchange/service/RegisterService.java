package com.kodilla.currencyexchange.service;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@Service
public class RegisterService {

    private final HttpClient client = HttpClient.newHttpClient();


    public void registerUser(String firstname, String lastname, String login,
                             String password, String emailAddress, UI ui) {
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/users"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{\"firstname\":\"" + firstname + "\", \"lastname\":\"" + lastname +
                                    "\", \"login\":\"" + login + "\", \"password\":\"" + password + "\", \"emailAddress\":\"" + emailAddress + "\"}"
                    ))
                    .build();
        } catch (Exception e) {
            ui.access(() -> Notification.show("Invalid URL or Request", 3000, Notification.Position.MIDDLE));
            return;
        }

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> ui.access(() -> handleRegisterResponse(response, ui))) // Use the passed UI instance
                .exceptionally(ex -> {
                    ui.access(() -> Notification.show("Failed to connect to server: " + ex.getMessage(), 5000, Notification.Position.MIDDLE));
                    return null;
                });
    }

    private void handleRegisterResponse(String response, UI ui) {
        ui.access(() -> {
            if (response.equals("User registered successfully")) {
                Notification.show(response, 3000, Notification.Position.MIDDLE);
                ui.navigate("login");
            } else {
                Notification.show(response, 3000, Notification.Position.MIDDLE);
            }
        });
    }
}
