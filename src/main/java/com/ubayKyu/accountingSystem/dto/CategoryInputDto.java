package com.ubayKyu.accountingSystem.dto;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CategoryInputDto {

    private UUID categoryId;

    @Size(max = 20, message = "*長度最多20個字元")
    @NotBlank(message = "*不可為空")
    private String caption;
    private String remark;
    
    public UUID getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
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
