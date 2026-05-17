package com.example.purchase_service.dto;

public class CheckoutResponse {

    private String purchaseId;
    private String status;
    private String message;

    public CheckoutResponse() {}

    public CheckoutResponse(String purchaseId, String status, String message) {
        this.purchaseId = purchaseId;
        this.status = status;
        this.message = message;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}