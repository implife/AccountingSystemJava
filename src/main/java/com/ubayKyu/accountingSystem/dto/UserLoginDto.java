package com.ubayKyu.accountingSystem.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserLoginDto {
    
    @Size(min = 3, max = 10, message = "*長度須為3 - 10個字元")
    @Pattern(regexp = "\\w*", message = "*只能是A-z 0-9")
    private String account;

    @Size(min = 5, max = 15, message = "*長度須為5 - 15個字元")
    @Pattern(regexp = "\\w*", message = "*只能是A-z 0-9")
    private String password;

    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
