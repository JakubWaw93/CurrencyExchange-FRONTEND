package com.kodilla.currencyexchange.View;

import com.kodilla.currencyexchange.service.RegisterService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("register")
@PageTitle("Register")
public class CreateAccountView extends VerticalLayout {

    private final RegisterService registerService = new RegisterService();

    public CreateAccountView() {
        TextField firstnameField = new TextField("First name");
        TextField lastnameField = new TextField("Last name");
        TextField loginField = new TextField("Login");
        PasswordField passwordField = new PasswordField("Password");
        EmailField emailField = new EmailField("Email Address");

        Button registerButton = new Button("Register", event -> {

            UI ui = UI.getCurrent();
            registerService.registerUser(firstnameField.getValue(), lastnameField.getValue(), loginField.getValue(), passwordField.getValue(), emailField.getValue(), ui);
        });

        add(firstnameField, lastnameField, loginField, passwordField, emailField, registerButton);
    }

}
