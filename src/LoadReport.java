import java.util.List;

public class LoadReport {
    private final List<Donation> donations;
    private final List<FoodRequest> requests;
    private final int skippedRecords;
    private final boolean donationsFileFound;
    private final boolean requestsFileFound;

    public LoadReport(List<Donation> donations, List<FoodRequest> requests,
                      int skippedRecords, boolean donationsFileFound,
                      boolean requestsFileFound) {
        this.donations = donations;
        this.requests = requests;
        this.skippedRecords = skippedRecords;
        this.donationsFileFound = donationsFileFound;
        this.requestsFileFound = requestsFileFound;
    }

    public List<Donation> getDonations() {
        return donations;
    }

    public List<FoodRequest> getRequests() {
        return requests;
    }

    public int getSkippedRecords() {
        return skippedRecords;
    }

    public boolean isDonationsFileFound() {
        return donationsFileFound;
    }

    public boolean isRequestsFileFound() {
        return requestsFileFound;
    }

    public boolean hasAnyDataFile() {
        return donationsFileFound || requestsFileFound;
    }
}
