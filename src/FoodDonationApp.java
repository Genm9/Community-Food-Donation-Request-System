import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main JavaFX application entry point.
 * This class owns the ONE shared DataManager used by every screen.
 */
public class FoodDonationApp extends Application implements AppNavigator {
    private final DataManager dataManager = new DataManager();
    private Stage primaryStage;
    private String statusMessage = "Ready.";
    private String loggedInUsername;
    private String loggedInRole;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Community Food Donation and Request System");
        primaryStage.setMinWidth(940);
        primaryStage.setMinHeight(620);

        loadInitialData();
        showLogin();
        primaryStage.show();
    }

    @Override
    public void showLogin() {
        loggedInUsername = null;
        setRoot(new LoginView(dataManager, this).build());
    }

    @Override
    public void showRegister() {
        setRoot(new RegisterView(dataManager, this).build());
    }

    @Override
    public void showDashboard() {
        if ("Admin".equalsIgnoreCase(loggedInRole)) {
            setRoot(new AdminDashboardView(dataManager, this).build());
        } else {
            setRoot(new DashboardView(dataManager, this).build());
        }
    }

    @Override
    public void completeLogin(String username, String role) {
        loggedInUsername = username;
        loggedInRole = role;
        statusMessage = "Welcome, " + username + ".";
        showDashboard();
    }

    @Override
    public void showDonationForm() {
        // Replace DonationFormView with the final Donation Module view when integrating team code.
        setRoot(new DonationFormView(dataManager, this).build());
    }

    @Override
    public void showRequestForm() {
        // Replace RequestFormView with the final Food Request Module view when integrating team code.
        setRoot(new RequestFormView(dataManager, this).build());
    }

    @Override
    public void showAdminView() {
        if (!"Admin".equalsIgnoreCase(loggedInRole)) {
            AlertUtil.showWarning("Access Denied",
                    "Only an administrator can open the records management screen.");
            return;
        }
        setRoot(new AdminView(dataManager, this).build());
    }

    @Override
    public void showSummaryView() {
        setRoot(new SummaryView(dataManager, this).build());
    }

    @Override
    public void saveData() {
        try {
            dataManager.saveAllData();
            statusMessage = "Data saved successfully.";
            AlertUtil.showInformation("Data Saved",
                    "All donation and food request records were saved successfully.");
        } catch (IOException ex) {
            statusMessage = "Unable to save data.";
            AlertUtil.showError("Save Error",
                    "Unable to save data. Please check the file location and try again.\n\n"
                            + ex.getMessage());
        }
    }

    @Override
    public void loadData() {
        boolean hasCurrentRecords = dataManager.getDonationCount() > 0 || dataManager.getRequestCount() > 0;
        if (hasCurrentRecords && !AlertUtil.showConfirmation("Load Saved Data",
                "Loading data will replace the current in-memory records. Continue?")) {
            return;
        }

        try {
            LoadReport report = dataManager.loadAllData();
            if (!report.hasAnyDataFile()) {
                statusMessage = "No saved data was found. The system started with empty records.";
                AlertUtil.showInformation("No Saved Data",
                        "No saved data was found. The system will start with empty records.");
            } else {
                statusMessage = "Data loaded successfully.";
                String message = "Loaded " + report.getDonations().size() + " donation(s) and "
                        + report.getRequests().size() + " food request(s).";
                if (report.getSkippedRecords() > 0) {
                    message += "\n\n" + report.getSkippedRecords()
                            + " malformed record(s) were skipped safely.";
                }
                AlertUtil.showInformation("Data Loaded", message);
            }
            showDashboard();
        } catch (IOException ex) {
            statusMessage = "Unable to load data.";
            AlertUtil.showError("Load Error",
                    "Unable to load data. Please check the file location and try again.\n\n"
                            + ex.getMessage());
        }
    }

    @Override
    public String getLoggedInUsername() {
        return loggedInUsername == null ? "Guest" : loggedInUsername;
    }

    @Override
    public String getLoggedInRole() {
        return loggedInRole == null ? "Guest" : loggedInRole;
    }

    @Override
    public void logout() {
        loggedInUsername = null;
        loggedInRole = null;
        statusMessage = "You have been logged out.";
        showLogin();
    }

    @Override
    public void exitApplication() {
        boolean confirmed = AlertUtil.showConfirmation("Exit Application",
                "Current data will be saved before the application closes. Continue?");
        if (!confirmed) {
            return;
        }

        try {
            dataManager.saveAllData();
            Platform.exit();
        } catch (IOException ex) {
            AlertUtil.showError("Save Error",
                    "The application cannot close because data could not be saved.\n\n"
                            + ex.getMessage());
        }
    }

    @Override
    public String getStatusMessage() {
        return statusMessage;
    }

    private void loadInitialData() {
        try {
            LoadReport report = dataManager.loadAllData();
            if (report.hasAnyDataFile()) {
                statusMessage = "Saved data loaded at startup.";
                if (report.getSkippedRecords() > 0) {
                    statusMessage += " " + report.getSkippedRecords()
                            + " malformed record(s) were skipped.";
                }
            } else {
                statusMessage = "No saved data was found. Ready for new records.";
            }

            if (dataManager.ensureDefaultAdminAccount()) {
                dataManager.saveAccounts();
                statusMessage += " Default administrator account is ready for demonstration.";
            }
        } catch (IOException ex) {
            statusMessage = "Could not load saved data. The system started with empty records.";
        }
    }

	private void setRoot(Parent root) {
        if (primaryStage.getScene() == null) {
            Scene scene = new Scene(root, 1020, 680);
            primaryStage.setScene(scene);
        } else {
            primaryStage.getScene().setRoot(root);
        }
    }
}
