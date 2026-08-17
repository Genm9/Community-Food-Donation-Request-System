import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * System summary and history screen with role-aware navigation and CSV export.
 */
public class SummaryView {

    private final DataManager dataManager;
    private final AppNavigator navigator;

    public SummaryView(DataManager dataManager, AppNavigator navigator) {
        this.dataManager = dataManager;
        this.navigator = navigator;
    }

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setStyle(UiStyle.pageBackground());

        root.setTop(createHeader());
        root.setLeft(createNavigation());
        root.setCenter(createContent());
        root.setBottom(createFooter());

        return root;
    }

    private HBox createHeader() {
        Label title = new Label("System Summary & History");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("A clear view of current donations, requests, and status activity");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #DBEAFE;");

        VBox heading = new VBox(4, title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label pagePill = new Label("SUMMARY");
        pagePill.setStyle("-fx-background-color: rgba(255,255,255,0.16); -fx-text-fill: white;"
                + " -fx-font-weight: bold; -fx-padding: 9 14; -fx-background-radius: 18;");

        HBox header = new HBox(18, heading, spacer, pagePill);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(24, 30, 24, 30));
        header.setStyle(UiStyle.header("#1D4ED8", "#2563EB"));

        return header;
    }

    private VBox createContent() {
        TextArea summaryArea = new TextArea(createSummaryText());
        summaryArea.setEditable(false);
        summaryArea.setWrapText(true);
        summaryArea.setPrefRowCount(18);
        summaryArea.setStyle("-fx-control-inner-background: #FFFFFF; -fx-background-color: #FFFFFF;"
                + " -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-background-radius: 12;"
                + " -fx-font-family: 'Consolas'; -fx-font-size: 13px; -fx-padding: 14;");

        VBox.setVgrow(summaryArea, Priority.ALWAYS);

        Button refreshButton = UiStyle.primaryButton("Refresh Summary", UiStyle.USER_ACCENT,
                () -> summaryArea.setText(createSummaryText()));

        Button saveButton = UiStyle.secondaryButton("Save Data", navigator::saveData);
        
        // --- 新增的 Export CSV 按钮 ---
        Button exportButton = UiStyle.secondaryButton("Export CSV", this::exportToCsv);

        Button backButton = UiStyle.secondaryButton("Back to Dashboard", navigator::showDashboard);

        // 将 exportButton 加入到操作栏中
        HBox actions = new HBox(10, refreshButton, saveButton, exportButton);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        actions.getChildren().addAll(spacer, backButton);

        Label heading = UiStyle.pageTitle("Community activity");
        Label subtitle = UiStyle.pageSubtitle("Refresh the report whenever you want the latest record totals, or export it as a CSV file.");

        VBox content = new VBox(10, heading, subtitle, summaryArea, actions);
        content.setPadding(new Insets(30));

        return content;
    }

    private VBox createNavigation() {
        if ("Admin".equalsIgnoreCase(navigator.getLoggedInRole())) {
            return NavigationMenu.adminMenu(navigator, "summary");
        }
        return NavigationMenu.userMenu(navigator, "summary");
    }

    private HBox createFooter() {
        Label footerText = new Label("Current user: " + navigator.getLoggedInUsername()
                + "     Role: " + navigator.getLoggedInRole());
        footerText.setStyle("-fx-text-fill: " + UiStyle.MUTED_TEXT + "; -fx-font-size: 12px;");

        HBox footer = new HBox(footerText);
        footer.setPadding(new Insets(11, 24, 11, 24));
        footer.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0;"
                + " -fx-border-width: 1 0 0 0;");

        return footer;
    }

    private String createSummaryText() {
        StringBuilder summary = new StringBuilder();

        summary.append("COMMUNITY FOOD SYSTEM SUMMARY\n");
        summary.append("================================\n\n");

        summary.append("Total Donations: ").append(dataManager.getDonationCount()).append("\n");
        summary.append("Total Donated Quantity: ").append(dataManager.getTotalDonatedQuantity())
                .append(" item(s)\n");
        summary.append("Total Food Requests: ").append(dataManager.getRequestCount()).append("\n\n");

        summary.append("REQUEST STATUS\n");
        summary.append("--------------\n");
        summary.append("Pending: ").append(dataManager.getPendingRequestCount()).append("\n");
        summary.append("Approved: ").append(dataManager.getApprovedRequestCount()).append("\n");
        summary.append("Fulfilled: ").append(dataManager.getFulfilledRequestCount()).append("\n");
        summary.append("Rejected: ").append(dataManager.getRejectedRequestCount()).append("\n\n");

        summary.append("CURRENT FOOD REQUEST HISTORY\n");
        summary.append("----------------------------\n");
        if (dataManager.getRequests().isEmpty()) {
            summary.append("No food requests have been recorded.\n");
        } else {
            for (FoodRequest request : dataManager.getRequests()) {
                summary.append(request.toDisplayString()).append("\n");
            }
        }

        summary.append("\nCURRENT DONATION HISTORY\n");
        summary.append("------------------------\n");
        if (dataManager.getDonations().isEmpty()) {
            summary.append("No donations have been recorded.\n");
        } else {
            for (Donation donation : dataManager.getDonations()) {
                summary.append(donation.toDisplayString()).append("\n");
            }
        }

        return summary.toString();
    }

    private void exportToCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Summary to CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("CommunityFood_Summary.csv");

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8)) {
                writer.write('\ufeff');

                writer.println("--- FOOD REQUESTS ---");
                writer.println("Request ID,Requester Name,Phone,Family Size,Category Needed,Urgency,Status,Notes");
                for (FoodRequest req : dataManager.getRequests()) {
                    writer.printf("%s,%s,%s,%d,%s,%s,%s,%s%n",
                            escapeCsv(req.getRequestId()), escapeCsv(req.getRequesterName()),
                            escapeCsv(req.getPhone()), req.getFamilySize(),
                            escapeCsv(req.getCategoryNeeded()), escapeCsv(req.getUrgency()),
                            escapeCsv(req.getStatus()), escapeCsv(req.getNotes()));
                }

                writer.println();

                writer.println("--- DONATIONS ---");
                writer.println("Donation ID,Donor Name,Phone,Food Name,Category,Quantity,Expiry Date,Notes,Pickup Location,Date Submitted,Status");
                for (Donation don : dataManager.getDonations()) {
                    writer.printf("%s,%s,%s,%s,%s,%d,%s,%s,%s,%s,%s%n",
                            escapeCsv(don.getDonationId()), escapeCsv(don.getDonorName()),
                            escapeCsv(don.getPhone()), escapeCsv(don.getFoodName()),
                            escapeCsv(don.getCategory()), don.getQuantity(),
                            escapeCsv(don.getExpiryDate()), escapeCsv(don.getNotes()),
                            escapeCsv(don.getPickupLocation()), escapeCsv(don.getDateSubmitted()),
                            escapeCsv(don.getStatus()));
                }

                AlertUtil.showInformation("Export Successful", "Data exported successfully to:\n" + file.getAbsolutePath());
            } catch (IOException ex) {
                AlertUtil.showError("Export Error", "Failed to save CSV file:\n" + ex.getMessage());
            }
        }
    }

    private String escapeCsv(String data) {
        if (data == null) {
            return "";
        }
        String escapedData = data.replace("\"", "\"\"");
        if (data.contains(",") || data.contains("\"") || data.contains("\n")) {
            escapedData = "\"" + escapedData + "\"";
        }
        return escapedData;
    }
}