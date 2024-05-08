package com.kodilla.currencyexchange.service;

import com.kodilla.currencyexchange.domain.UserDto;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final WebClient webClient;

    public UserService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<UserDto> getUserById(Long userId) {
        return webClient.get()
                .uri("/users/{userId}", userId)
                .retrieve()
                .bodyToMono(UserDto.class);
    }

    public Mono<UserDto> getUserByEmail(String email) {

        return webClient.get()
                .uri("/users/email/{email}", email)
                .retrieve()
                .bodyToMono(UserDto.class);
    }

    public void registerUser(String firstname, String lastname, String login,
                             String password, String emailAddress, UI ui) {
        webClient.post()
                .uri("/users")
                .header("Content-Type", "application/json")
                .bodyValue(createUserJson(firstname, lastname, login, password, emailAddress))
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(response -> ui.access(() -> handleRegisterResponse(response, ui)),
                        error -> ui.access(() -> Notification.show("Failed to connect to server: " + error.getMessage(), 5000, Notification.Position.MIDDLE)));
    }

    private String createUserJson(String firstname, String lastname, String login, String password, String emailAddress) {
        return String.format("{\"firstname\":\"%s\", \"lastname\":\"%s\", \"login\":\"%s\", \"password\":\"%s\", \"emailAddress\":\"%s\"}",
                firstname, lastname, login, password, emailAddress);
    }

    private void handleRegisterResponse(String response, UI ui) {
        if ("User registered successfully".equals(response)) {
            Notification.show(response, 3000, Notification.Position.MIDDLE);
            ui.navigate("login");
        } else {
            Notification.show(response, 3000, Notification.Position.MIDDLE);
        }
    }

    public Mono<Void> deleteUser(Long userId) {
        return webClient.delete()
                .uri("/users/{userId}", userId)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Boolean> authenticate(String login, String password) {
        return webClient.post()
                .uri("/users/auth")
                .header("Content-Type", "application/json")
                .bodyValue("{\"login\":\"" + login + "\", \"password\":\"" + password + "\"}")
                .retrieve()
                .bodyToMono(String.class)
                .map("Authentication successful"::equals)
                .onErrorReturn(false);
    }
}
