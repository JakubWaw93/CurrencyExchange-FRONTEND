package com.kodilla.currencyexchange.domain;



import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class UserDto {

    private Long id;
    private String firstname;
    private String lastname;
    private String login;
    private String password;
    private String emailAddress;
    @Builder.Default
    private List<Long> transactionsIds = new ArrayList<>();
    private String apiKey;
    @Builder.Default
    private boolean active = true;

    public UserDto(Long id, String firstname, String lastname, String login, String password, String emailAddress,
                   List<Long> transactionsIds, String apiKey, boolean active) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.login = login;
        this.password = password;
        this.emailAddress = emailAddress;
        this.transactionsIds = new ArrayList<>();
        this.apiKey = apiKey;
        this.active = true;
    }
}