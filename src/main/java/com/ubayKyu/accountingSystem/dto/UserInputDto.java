package com.ubayKyu.accountingSystem.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserInputDto {
    private UUID userId;

    @Size(max = 20, message = "*長度最多20個字元")
    @NotBlank
    private String account;

    @Size(max = 20, message = "*長度最多20個字元")
    @NotBlank
    private String name;

    @Pattern(regexp = "^(.+)@(.+)$", message = "*email格式不正確")
    private String email;

    private String level = "general";
    private String createDate = "--";
    private String modifyDate = "--";
    
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
    public String getLevel() {
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
    }
    public void setLevel(int level) {
        this.level = level == 0 ? "manager" : "general";
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getCreateDate() {
        return createDate;
    }
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
    }
    public String getModifyDate() {
        return modifyDate;
    }
    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }
    public void setModifyDate(LocalDateTime modifyDate) {
        this.modifyDate = modifyDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
    }
    
}
