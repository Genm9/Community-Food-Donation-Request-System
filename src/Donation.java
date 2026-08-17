/**
 * Represents one food donation submitted to the community food centre.
 *
 * The first eight fields remain compatible with the original application.
 * The additional fields preserve the teammate donation module's pickup,
 * submission-date, and status information.
 */
public class Donation {
    private final String donationId;
    private final String donorName;
    private final String phone;
    private final String foodName;
    private final String category;
    private final int quantity;
    private final String expiryDate;
    private final String notes;
    private final String pickupLocation;
    private final String dateSubmitted;
    private final String status;

    /**
     * Original constructor retained for existing integrations and old records.
     */
    public Donation(String donationId, String donorName, String phone,
                    String foodName, String category, int quantity,
                    String expiryDate, String notes) {
        this(donationId, donorName, phone, foodName, category, quantity,
                expiryDate, notes, "", "", "PENDING");
    }

    /**
     * Full constructor used by the integrated FoodDonation donation module.
     */
    public Donation(String donationId, String donorName, String phone,
                    String foodName, String category, int quantity,
                    String expiryDate, String notes, String pickupLocation,
                    String dateSubmitted, String status) {
        this.donationId = safe(donationId);
        this.donorName = safe(donorName);
        this.phone = safe(phone);
        this.foodName = safe(foodName);
        this.category = safe(category);
        this.quantity = quantity;
        this.expiryDate = safe(expiryDate);
        this.notes = safe(notes);
        this.pickupLocation = safe(pickupLocation);
        this.dateSubmitted = safe(dateSubmitted);
        this.status = status == null || status.isBlank() ? "PENDING" : status;
    }

    public String getDonationId() {
        return donationId;
    }

    public String getDonorName() {
        return donorName;
    }

    public String getPhone() {
        return phone;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getNotes() {
        return notes;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getDateSubmitted() {
        return dateSubmitted;
    }

    public String getStatus() {
        return status;
    }

    /**
     * Returns one safe pipe-separated line for donations.txt.
     * The first eight columns stay in the original order; new columns are
     * appended so existing files can still be loaded.
     */
    public String toFileString() {
        return String.join("|",
                FileStorageService.sanitize(donationId),
                FileStorageService.sanitize(donorName),
                FileStorageService.sanitize(phone),
                FileStorageService.sanitize(foodName),
                FileStorageService.sanitize(category),
                String.valueOf(quantity),
                FileStorageService.sanitize(expiryDate),
                FileStorageService.sanitize(notes),
                FileStorageService.sanitize(pickupLocation),
                FileStorageService.sanitize(dateSubmitted),
                FileStorageService.sanitize(status));
    }

    public String toDisplayString() {
        return donationId + " | " + donorName + " | " + foodName
                + " | " + category + " | Quantity: " + quantity
                + " | Expiry: " + expiryDate;
    }

    @Override
    public String toString() {
        return toDisplayString();
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
