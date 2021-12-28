package com.ubayKyu.accountingSystem.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserRowDto {
    private String userId;
    private String account;
    private String name;
    private String email;
    private String level;
    private String createDate;
    
    public UserRowDto(String userId, String account, String name, String email, String level, LocalDateTime createDate) {
        this.userId = userId;
        this.account = account;
        this.name = name;
        this.email = email;
        this.level = level;
        this.createDate = createDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
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
    public String getLevel() {
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
    }
    public String getCreateDate() {
        return createDate;
    }
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    
}
