package com.ubayKyu.accountingSystem.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CategoryRowDto {
    private String createDate;
    private String categoryName;
    private int accountingCount;
    private String categoryID;
    
    public CategoryRowDto(LocalDateTime createDate, String categoryName, int accountingCount, String categoryID) {
        this.createDate = createDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        this.categoryName = categoryName;
        this.accountingCount = accountingCount;
        this.categoryID = categoryID;
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
    public int getAccountingCount() {
        return accountingCount;
    }
    public void setAccountingCount(int accountingCount) {
        this.accountingCount = accountingCount;
    }
    public String getCategoryID() {
        return categoryID;
    }
    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }
}
