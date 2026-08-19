public class FoodRequest {
    private final String requestId;
    private final String requesterName;
    private final String phone;
    private final int familySize;
    private final String categoryNeeded;
    private final String urgency;
    private final String notes;
    private String status;
    private final String foodItem;
    private final int quantity;
    private final String dateSubmitted;


    public FoodRequest(String requestId, String requesterName, String phone,
                       int familySize, String categoryNeeded, String urgency,
                       String notes, String status) {
        this(requestId, requesterName, phone, familySize, categoryNeeded,
                urgency, notes, status, "", 0, "");
    }


    public FoodRequest(String requestId, String requesterName, String phone,
                       int familySize, String categoryNeeded, String urgency,
                       String notes, String status, String foodItem, int quantity,
                       String dateSubmitted) {
        this.requestId = safe(requestId);
        this.requesterName = safe(requesterName);
        this.phone = safe(phone);
        this.familySize = familySize;
        this.categoryNeeded = safe(categoryNeeded);
        this.urgency = safe(urgency);
        this.notes = safe(notes);
        this.status = status == null || status.isBlank() ? "Pending" : status;
        this.foodItem = safe(foodItem);
        this.quantity = quantity;
        this.dateSubmitted = safe(dateSubmitted);
    }

    public String getRequestId() {
        return requestId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public String getPhone() {
        return phone;
    }

    public int getFamilySize() {
        return familySize;
    }

    public String getCategoryNeeded() {
        return categoryNeeded;
    }

    public String getUrgency() {
        return urgency;
    }

    public String getNotes() {
        return notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFoodItem() {
        return foodItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDateSubmitted() {
        return dateSubmitted;
    }


    public String toFileString() {
        return String.join("|",
                FileStorageService.sanitize(requestId),
                FileStorageService.sanitize(requesterName),
                FileStorageService.sanitize(phone),
                String.valueOf(familySize),
                FileStorageService.sanitize(categoryNeeded),
                FileStorageService.sanitize(urgency),
                FileStorageService.sanitize(notes),
                FileStorageService.sanitize(status),
                FileStorageService.sanitize(foodItem),
                String.valueOf(quantity),
                FileStorageService.sanitize(dateSubmitted));
    }

    public String toDisplayString() {
        if (!foodItem.isBlank() || quantity > 0) {
            return requestId + " | " + requesterName + " | " + foodItem
                    + " | Quantity: " + quantity + " | Urgency: " + urgency
                    + " | Status: " + status;
        }
        return requestId + " | " + requesterName + " | " + categoryNeeded
                + " | Family: " + familySize + " | Urgency: " + urgency
                + " | Status: " + status;
    }

    @Override
    public String toString() {
        return toDisplayString();
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
