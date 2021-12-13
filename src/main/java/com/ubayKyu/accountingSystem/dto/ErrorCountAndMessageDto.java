package com.ubayKyu.accountingSystem.dto;

import java.util.List;

public class ErrorCountAndMessageDto {
    private int successCount;
    private int failedCount;
    private List<String> errMessages;
    
    public int getSuccessCount() {
        return successCount;
    }
    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }
    public int getFailedCount() {
        return failedCount;
    }
    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }
    public List<String> getErrMessages() {
        return errMessages;
    }
    public void setErrMessages(List<String> errMessages) {
        this.errMessages = errMessages;
    }
}
