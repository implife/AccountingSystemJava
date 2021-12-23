package com.ubayKyu.accountingSystem.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class AccountingInputDto {

    private int accountingId;
    private String inout = "in";

    @NotBlank
    private String categoryName;

    @Min(value = 1, message = "*金額須 > 0")
    @Max(value = 10000000, message = "*金額不可超過一千萬")
    private int amount;

    @NotBlank
    private String caption;
    private String remark;

    public Integer getAccountingId() {
        return accountingId;
    }
    public void setAccountingId(Integer accountingId) {
        this.accountingId = accountingId;
    }
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
