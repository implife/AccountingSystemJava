package com.ubayKyu.accountingSystem.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AccountingRowDto {
    private int accountingId;
    private String createDate;
    private String categoryName;
    private String type;
    private int amount;
    private String caption;

    public AccountingRowDto(int accountingId, LocalDateTime createDate, String categoryName, String type, int amount,
            String caption) {
        this.accountingId = accountingId;
        this.createDate = createDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        this.categoryName = categoryName;
        this.type = type;
        this.amount = Math.abs(amount);
        this.caption = caption;
    }
    public int getAccountingId() {
        return accountingId;
    }
    public void setAccountingId(int accountingId) {
        this.accountingId = accountingId;
    }
    public String getCreateDate() {
        return createDate;
    }
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public String getCaption() {
        return caption;
    }
    public void setCaption(String caption) {
        this.caption = caption;
    }

}
