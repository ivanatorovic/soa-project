package com.soa.tour_service.events;
import java.util.List;

public class ToursReservationCancelEvent {
    private String purchaseId;
    private Long touristId;
    private List<PurchaseTourItem> items;
    private String reason;

    public ToursReservationCancelEvent() {}

    public ToursReservationCancelEvent(String purchaseId, Long touristId, List<PurchaseTourItem> items, String reason) {
        this.purchaseId = purchaseId;
        this.touristId = touristId;
        this.items = items;
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

    public List<PurchaseTourItem> getItems() {
        return items;
    }

    public void setItems(List<PurchaseTourItem> items) {
        this.items = items;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
