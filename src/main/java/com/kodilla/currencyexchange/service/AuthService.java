package com.kodilla.currencyexchange.service;

import com.vaadin.flow.component.notification.Notification;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AuthService {

    private HttpClient client = HttpClient.newHttpClient();

    public boolean authenticate(String login, String password) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/users/auth"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{\"login\":\"" + login + "\", \"password\":\"" + password + "\"}"
                    ))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return "Authentication successful".equals(response.body());
        } catch (Exception e) {
            Notification.show("Invalid URL or Request", 3000, Notification.Position.MIDDLE);
            return false;
        }
    }

}
