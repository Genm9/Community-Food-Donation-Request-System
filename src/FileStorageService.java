import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public final class FileStorageService {
    private static final Path DATA_DIRECTORY = Paths.get("data");
    private static final Path DONATIONS_FILE = DATA_DIRECTORY.resolve("donations.txt");
    private static final Path REQUESTS_FILE = DATA_DIRECTORY.resolve("requests.txt");
    private static final Path ACCOUNTS_FILE = DATA_DIRECTORY.resolve("accounts.txt");

    private FileStorageService() {
        // Utility class: no object should be created.
    }

    public static void saveAll(List<Donation> donations,
                               List<FoodRequest> requests) throws IOException {
        Files.createDirectories(DATA_DIRECTORY);

        try (PrintWriter donationWriter = new PrintWriter(
                Files.newBufferedWriter(DONATIONS_FILE, StandardCharsets.UTF_8));
             PrintWriter requestWriter = new PrintWriter(
                     Files.newBufferedWriter(REQUESTS_FILE, StandardCharsets.UTF_8))) {

            for (Donation donation : donations) {
                donationWriter.println(donation.toFileString());
            }

            for (FoodRequest request : requests) {
                requestWriter.println(request.toFileString());
            }
        }
    }

    public static void saveAccounts(List<UserAccount> accounts) throws IOException {
        Files.createDirectories(DATA_DIRECTORY);

        try (PrintWriter writer = new PrintWriter(
                Files.newBufferedWriter(ACCOUNTS_FILE, StandardCharsets.UTF_8))) {
            for (UserAccount account : accounts) {
                writer.println(account.toFileString());
            }
        }
    }

    public static List<UserAccount> loadAccounts() throws IOException {
        List<UserAccount> accounts = new ArrayList<>();
        if (!Files.exists(ACCOUNTS_FILE)) {
            return accounts;
        }

        try (Scanner scanner = new Scanner(ACCOUNTS_FILE, StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    String[] parts = line.split("\\|", -1);
                    if (parts.length != 3 || parts[0].isBlank() || parts[1].isBlank()) {
                        throw new InvalidDataFormatException(
                                "An account record must contain a username, password and role.");
                    }
                    accounts.add(new UserAccount(parts[0], parts[1], parts[2]));
                } catch (InvalidDataFormatException ex) {
                    // Ignore malformed account records and continue loading valid users.
                }
            }
        }

        return accounts;
    }

    public static LoadReport loadAll() throws IOException {
        List<Donation> loadedDonations = new ArrayList<>();
        List<FoodRequest> loadedRequests = new ArrayList<>();
        boolean donationsFileFound = Files.exists(DONATIONS_FILE);
        boolean requestsFileFound = Files.exists(REQUESTS_FILE);

        int skippedRecords = 0;
        if (donationsFileFound) {
            skippedRecords += loadDonations(loadedDonations);
        }
        if (requestsFileFound) {
            skippedRecords += loadRequests(loadedRequests);
        }

        return new LoadReport(loadedDonations, loadedRequests, skippedRecords,
                donationsFileFound, requestsFileFound);
    }

    private static int loadDonations(List<Donation> donations) throws IOException {
        int skippedRecords = 0;

        try (Scanner scanner = new Scanner(DONATIONS_FILE, StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    String[] parts = line.split("\\|", -1);
                    if (parts.length != 8 && parts.length != 11) {
                        throw new InvalidDataFormatException(
                                "A donation record must contain 8 or 11 fields.");
                    }

                    int quantity = Integer.parseInt(parts[5]);
                    if (quantity <= 0) {
                        throw new InvalidDataFormatException(
                                "Donation quantity must be greater than zero.");
                    }

                    if (parts.length == 8) {
                        donations.add(new Donation(parts[0], parts[1], parts[2],
                                parts[3], parts[4], quantity, parts[6], parts[7]));
                    } else {
                        donations.add(new Donation(parts[0], parts[1], parts[2],
                                parts[3], parts[4], quantity, parts[6], parts[7],
                                parts[8], parts[9], parts[10]));
                    }
                } catch (InvalidDataFormatException | NumberFormatException ex) {
                    skippedRecords++;
                }
            }
        }

        return skippedRecords;
    }

    private static int loadRequests(List<FoodRequest> requests) throws IOException {
        int skippedRecords = 0;

        try (Scanner scanner = new Scanner(REQUESTS_FILE, StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    String[] parts = line.split("\\|", -1);
                    if (parts.length != 8 && parts.length != 11) {
                        throw new InvalidDataFormatException(
                                "A food request record must contain 8 or 11 fields.");
                    }

                    int familySize = Integer.parseInt(parts[3]);
                    if (familySize <= 0) {
                        throw new InvalidDataFormatException(
                                "Family size must be greater than zero.");
                    }

                    if (parts.length == 8) {
                        requests.add(new FoodRequest(parts[0], parts[1], parts[2],
                                familySize, parts[4], parts[5], parts[6], parts[7]));
                    } else {
                        int quantity = Integer.parseInt(parts[9]);
                        if (quantity <= 0) {
                            throw new InvalidDataFormatException(
                                    "Request quantity must be greater than zero.");
                        }
                        requests.add(new FoodRequest(parts[0], parts[1], parts[2],
                                familySize, parts[4], parts[5], parts[6], parts[7],
                                parts[8], quantity, parts[10]));
                    }
                } catch (InvalidDataFormatException | NumberFormatException ex) {
                    skippedRecords++;
                }
            }
        }

        return skippedRecords;
    }


    public static String sanitize(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("|", "/")
                .replace("\r", " ")
                .replace("\n", " ")
                .trim();
    }
}
