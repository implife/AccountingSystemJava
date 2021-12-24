package com.ubayKyu.accountingSystem.dto;

import java.util.UUID;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserProfileDto {
    private UUID userId;
    private String account;

    @Size(min = 1, max = 20, message = "*長度須為1 - 20個字元")
    private String name;

    @Pattern(regexp = "^(.+)@(.+)$", message = "*email格式不正確")
    private String email;

    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
