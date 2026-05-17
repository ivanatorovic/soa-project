package com.example.purchase_service.events;

public class ToursReservationFailedEvent {
    private String purchaseId;
    private Long touristId;
    private String reason;

    public ToursReservationFailedEvent() {}

    public ToursReservationFailedEvent(String purchaseId, Long touristId, String reason) {
        this.purchaseId = purchaseId;
        this.touristId = touristId;
        this.reason = reason;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Long getTouristId() {
        return touristId;
    }

    public void setTouristId(Long touristId) {
        this.touristId = touristId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
