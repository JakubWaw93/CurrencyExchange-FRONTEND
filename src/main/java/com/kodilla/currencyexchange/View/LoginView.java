package com.kodilla.currencyexchange.View;

import com.kodilla.currencyexchange.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;



@Route("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout {

    private final UserService authenticate;

    public LoginView(UserService authenticate) {
        this.authenticate=authenticate;
        TextField loginField = new TextField("Login");
        PasswordField passwordField = new PasswordField("Password");

        Button loginButton = new Button("Log in", event -> loginUser(loginField.getValue(), passwordField.getValue()));
        Button registerButton = new Button("Register", event -> UI.getCurrent().navigate("register"));

        add(loginField, passwordField, loginButton, registerButton);
    }

    private void loginUser(String login, String password) {
        UI ui = UI.getCurrent();

        authenticate.authenticate(login, password).subscribe(authenticated -> {
            ui.access(() -> {
                if (authenticated) {
                    ui.navigate("main");
                } else {
                    Notification.show("Invalid login or password", 3000, Notification.Position.MIDDLE);
                }
            });
        }, error -> {
            ui.access(() -> Notification.show("An error occurred during login: " + error.getMessage(), 5000, Notification.Position.MIDDLE));
        });
    }

}
