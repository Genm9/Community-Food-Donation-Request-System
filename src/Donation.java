
/**
 * Represents one food donation submitted to the community food centre.
 * This class can be used directly by the Donation Module.
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

    public Donation(String donationId, String donorName, String phone,
                    String foodName, String category, int quantity,
                    String expiryDate, String notes) {
        this.donationId = donationId;
        this.donorName = donorName;
        this.phone = phone;
        this.foodName = foodName;
        this.category = category;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.notes = notes == null ? "" : notes;
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

    /**
     * Returns one safe pipe-separated line for donations.txt.
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
                FileStorageService.sanitize(notes));
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
}
