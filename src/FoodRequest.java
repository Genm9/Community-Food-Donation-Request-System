
/**
 * Represents one food assistance request.
 * The status can be updated by the administrator through the Admin Records screen.
 */
public class FoodRequest {
    private final String requestId;
    private final String requesterName;
    private final String phone;
    private final int familySize;
    private final String categoryNeeded;
    private final String urgency;
    private final String notes;
    private String status;

    public FoodRequest(String requestId, String requesterName, String phone,
                       int familySize, String categoryNeeded, String urgency,
                       String notes, String status) {
        this.requestId = requestId;
        this.requesterName = requesterName;
        this.phone = phone;
        this.familySize = familySize;
        this.categoryNeeded = categoryNeeded;
        this.urgency = urgency;
        this.notes = notes == null ? "" : notes;
        this.status = status == null || status.isBlank() ? "Pending" : status;
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

    /**
     * Returns one safe pipe-separated line for requests.txt.
     */
    public String toFileString() {
        return String.join("|",
                FileStorageService.sanitize(requestId),
                FileStorageService.sanitize(requesterName),
                FileStorageService.sanitize(phone),
                String.valueOf(familySize),
                FileStorageService.sanitize(categoryNeeded),
                FileStorageService.sanitize(urgency),
                FileStorageService.sanitize(notes),
                FileStorageService.sanitize(status));
    }

    public String toDisplayString() {
        return requestId + " | " + requesterName + " | " + categoryNeeded
                + " | Family: " + familySize + " | Urgency: " + urgency
                + " | Status: " + status;
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}
