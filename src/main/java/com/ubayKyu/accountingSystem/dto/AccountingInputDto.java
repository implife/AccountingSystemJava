package com.ubayKyu.accountingSystem.dto;

public class AccountingInputDto {

    private String inout = "in";
    private String categoryName;
    private int amount;
    private String caption;
    private String remark;

    public String getInout() {
        return inout;
    }
    public void setInout(String inout) {
        this.inout = inout;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }

}
