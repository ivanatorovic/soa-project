package com.example.purchase_service.dto;

public class PurchaseStatusResponse {

    private String purchaseId;
    private String status;
    private String failureReason;

    public PurchaseStatusResponse() {}

    public PurchaseStatusResponse(String purchaseId, String status, String failureReason) {
        this.purchaseId = purchaseId;
        this.status = status;
        this.failureReason = failureReason;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public String getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }
}