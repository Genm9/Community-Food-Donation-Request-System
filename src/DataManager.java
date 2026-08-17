
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Central in-memory data store for the application.
 * Create only ONE DataManager object in FoodDonationApp and pass it to every view.
 */
public class DataManager {
    private final List<Donation> donations = new ArrayList<>();
    private final List<FoodRequest> requests = new ArrayList<>();
    private final List<UserAccount> accounts = new ArrayList<>();

    public boolean usernameExists(String username) {
        return findAccount(username) != null;
    }

    /**
     * Creates the demonstration administrator account only when no admin account exists.
     * Normal users cannot register this reserved username in RegisterView.
     */
    public boolean ensureDefaultAdminAccount() {
        if (usernameExists("admin")) {
            return false;
        }
        accounts.add(new UserAccount("admin", "admin123", "Admin"));
        return true;
    }

    public void registerAccount(String username, String password) {
        if (usernameExists(username)) {
            throw new IllegalArgumentException("That username is already registered.");
        }
        accounts.add(new UserAccount(username, password, "User"));
    }

    public UserAccount authenticate(String username, String password) {
        for (UserAccount account : accounts) {
            if (account.getUsername().equalsIgnoreCase(username)
                    && account.getPassword().equals(password)) {
                return account;
            }
        }
        return null;
    }

    public List<UserAccount> getAccounts() {
        return new ArrayList<>(accounts);
    }

    private UserAccount findAccount(String username) {
        for (UserAccount account : accounts) {
            if (account.getUsername().equalsIgnoreCase(username)) {
                return account;
            }
        }
        return null;
    }

    public void addDonation(Donation donation) {
        if (donation == null) {
            throw new IllegalArgumentException("Donation cannot be null.");
        }
        if (findDonationById(donation.getDonationId()) != null) {
            throw new IllegalArgumentException("Donation ID already exists.");
        }
        donations.add(donation);
    }

    public void addRequest(FoodRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Food request cannot be null.");
        }
        if (findRequestById(request.getRequestId()) != null) {
            throw new IllegalArgumentException("Request ID already exists.");
        }
        requests.add(request);
    }

    public List<Donation> getDonations() {
        return new ArrayList<>(donations);
    }

    public List<FoodRequest> getRequests() {
        return new ArrayList<>(requests);
    }

    public Donation findDonationById(String donationId) {
        for (Donation donation : donations) {
            if (donation.getDonationId().equalsIgnoreCase(donationId)) {
                return donation;
            }
        }
        return null;
    }

    public FoodRequest findRequestById(String requestId) {
        for (FoodRequest request : requests) {
            if (request.getRequestId().equalsIgnoreCase(requestId)) {
                return request;
            }
        }
        return null;
    }

    public boolean deleteDonation(String donationId) {
        return donations.removeIf(donation ->
                donation.getDonationId().equalsIgnoreCase(donationId));
    }

    public boolean deleteRequest(String requestId) {
        return requests.removeIf(request ->
                request.getRequestId().equalsIgnoreCase(requestId));
    }

    public boolean updateRequestStatus(String requestId, String newStatus) {
        if (!isValidStatus(newStatus)) {
            throw new IllegalArgumentException("Invalid request status.");
        }

        FoodRequest request = findRequestById(requestId);
        if (request == null) {
            return false;
        }
        request.setStatus(newStatus);
        return true;
    }

    public List<Donation> searchDonations(String keyword) {
        String searchText = normalize(keyword);
        List<Donation> result = new ArrayList<>();

        for (Donation donation : donations) {
            String searchableText = (donation.getDonationId() + " "
                    + donation.getDonorName() + " "
                    + donation.getFoodName() + " "
                    + donation.getCategory()).toLowerCase();
            if (searchableText.contains(searchText)) {
                result.add(donation);
            }
        }
        return result;
    }

    public List<FoodRequest> searchRequests(String keyword) {
        String searchText = normalize(keyword);
        List<FoodRequest> result = new ArrayList<>();

        for (FoodRequest request : requests) {
            String searchableText = (request.getRequestId() + " "
                    + request.getRequesterName() + " "
                    + request.getCategoryNeeded() + " "
                    + request.getUrgency() + " "
                    + request.getStatus()).toLowerCase();
            if (searchableText.contains(searchText)) {
                result.add(request);
            }
        }
        return result;
    }

    public int getDonationCount() {
        return donations.size();
    }

    public int getRequestCount() {
        return requests.size();
    }

    public int getPendingRequestCount() {
        return getRequestCountByStatus("Pending");
    }

    public int getApprovedRequestCount() {
        return getRequestCountByStatus("Approved");
    }

    public int getFulfilledRequestCount() {
        return getRequestCountByStatus("Fulfilled");
    }

    public int getRejectedRequestCount() {
        return getRequestCountByStatus("Rejected");
    }

    public int getTotalDonatedQuantity() {
        int total = 0;
        for (Donation donation : donations) {
            total += donation.getQuantity();
        }
        return total;
    }

    public String generateDonationId() {
        return String.format("D%03d", getNextNumber("D", donations.size(), true));
    }

    public String generateRequestId() {
        return String.format("R%03d", getNextNumber("R", requests.size(), false));
    }

    public void saveAccounts() throws IOException {
        FileStorageService.saveAccounts(accounts);
    }

    public void saveAllData() throws IOException {
        FileStorageService.saveAll(donations, requests);
        FileStorageService.saveAccounts(accounts);
    }

    /**
     * Replaces the current in-memory data with the records read from text files.
     */
    public LoadReport loadAllData() throws IOException {
        LoadReport report = FileStorageService.loadAll();
        donations.clear();
        donations.addAll(report.getDonations());
        requests.clear();
        requests.addAll(report.getRequests());
        accounts.clear();
        accounts.addAll(FileStorageService.loadAccounts());
        return report;
    }

    private int getRequestCountByStatus(String status) {
        int count = 0;
        for (FoodRequest request : requests) {
            if (status.equalsIgnoreCase(request.getStatus())) {
                count++;
            }
        }
        return count;
    }

    private int getNextNumber(String prefix, int currentSize, boolean donation) {
        int highestNumber = currentSize;
        if (donation) {
            for (Donation donationRecord : donations) {
                highestNumber = Math.max(highestNumber,
                        extractNumber(donationRecord.getDonationId(), prefix));
            }
        } else {
            for (FoodRequest requestRecord : requests) {
                highestNumber = Math.max(highestNumber,
                        extractNumber(requestRecord.getRequestId(), prefix));
            }
        }
        return highestNumber + 1;
    }

    private int extractNumber(String id, String prefix) {
        try {
            if (id != null && id.toUpperCase().startsWith(prefix)) {
                return Integer.parseInt(id.substring(prefix.length()));
            }
        } catch (NumberFormatException ignored) {
            // A non-standard ID does not affect automatic ID generation.
        }
        return 0;
    }

    private boolean isValidStatus(String status) {
        return "Pending".equals(status)
                || "Approved".equals(status)
                || "Fulfilled".equals(status)
                || "Rejected".equals(status);
    }

    private String normalize(String text) {
        return text == null ? "" : text.trim().toLowerCase();
    }
}
