package com.example.purchase_service.events;
import java.util.List;

public class ToursReservedEvent {
    private String purchaseId;
    private Long touristId;
    private List<PurchaseTourItem> items;

    public ToursReservedEvent() {}

    public ToursReservedEvent(String purchaseId, Long touristId, List<PurchaseTourItem> items) {
        this.purchaseId = purchaseId;
        this.touristId = touristId;
        this.items = items;
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
}
