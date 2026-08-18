import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Modern administrator screen for searching, updating and deleting records.
 */
public class AdminView {
    private final DataManager dataManager;
    private final AppNavigator navigator;

    private final ComboBox<String> recordTypeBox = new ComboBox<>();
    private final TextField searchField = new TextField();
    private final ListView<String> recordList = new ListView<>();
    private final ComboBox<String> statusBox = new ComboBox<>();
    private final Label selectionDetails = new Label("Select a record to view its details.");

    private List<Donation> displayedDonations = new ArrayList<>();
    private List<FoodRequest> displayedRequests = new ArrayList<>();

    public AdminView(DataManager dataManager, AppNavigator navigator) {
        this.dataManager = dataManager;
        this.navigator = navigator;
    }

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setStyle(UiStyle.pageBackground());
        root.setTop(createHeader());
        root.setLeft(NavigationMenu.adminMenu(navigator, "records"));
        root.setCenter(createContent());
        root.setBottom(createFooter());

        configureControls();
        refreshRecords();
        return root;
    }

    private HBox createHeader() {
        Label title = new Label("Manage Records");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label subtitle = new Label("Search, review, update and manage community food records");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #E2E8F0;");
        VBox heading = new VBox(4, title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label pagePill = new Label("ADMIN RECORDS");
        pagePill.setStyle("-fx-background-color: rgba(255,255,255,0.16); -fx-text-fill: white;"
                + " -fx-font-weight: bold; -fx-padding: 9 14; -fx-background-radius: 18;");

        HBox header = new HBox(18, heading, spacer, pagePill);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(24, 30, 24, 30));
        header.setStyle(UiStyle.header("#334155", "#475569"));
        return header;
    }

    private VBox createContent() {
        recordTypeBox.getItems().setAll("Donations", "Food Requests");
        recordTypeBox.setValue("Donations");
        recordTypeBox.setPrefWidth(160);
        UiStyle.styleInput(recordTypeBox);

        searchField.setPromptText("Search by ID, name, category, or status");
        UiStyle.styleInput(searchField);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchButton = UiStyle.primaryButton("Search", UiStyle.ADMIN_ACCENT, this::refreshRecords);
        Button showAllButton = UiStyle.secondaryButton("Show All", () -> {
            searchField.clear();
            refreshRecords();
        });

        HBox searchBar = new HBox(10, new Label("Record Type"), recordTypeBox,
                searchField, searchButton, showAllButton);
        searchBar.setAlignment(Pos.CENTER_LEFT);

        recordList.setPrefHeight(260);
        recordList.setPlaceholder(new Label("No records found."));
        recordList.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0;"
                + " -fx-border-radius: 10; -fx-background-radius: 10;");

        selectionDetails.setWrapText(true);
        selectionDetails.setMinHeight(120);
        selectionDetails.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #E2E8F0;"
                + " -fx-padding: 16; -fx-background-radius: 10; -fx-border-radius: 10;"
                + " -fx-text-fill: #334155; -fx-font-size: 13px;");

        statusBox.getItems().setAll("Pending", "Approved", "Fulfilled", "Rejected");
        statusBox.setPromptText("Select new status");
        statusBox.setPrefWidth(165);
        UiStyle.styleInput(statusBox);

        Button updateStatusButton = UiStyle.primaryButton("Update Status", UiStyle.ADMIN_ACCENT,
                this::updateSelectedRequestStatus);
        Button deleteButton = UiStyle.dangerButton("Delete Selected", this::deleteSelectedRecord);
        Button backButton = UiStyle.secondaryButton("Back to Dashboard", navigator::showDashboard);

        HBox actions = new HBox(10, new Label("New Request Status"), statusBox,
                updateStatusButton, deleteButton, backButton);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(8, 0, 0, 0));

        Label heading = UiStyle.pageTitle("Records workspace");
        Label intro = UiStyle.pageSubtitle("Select a record to view details. Request status changes are available for Food Requests.");
        Label detailsHeading = UiStyle.sectionTitle("Selected record details");

        VBox card = new VBox(14, heading, intro, searchBar, recordList,
                detailsHeading, selectionDetails, actions);
        card.setPadding(new Insets(26));
        card.setStyle(UiStyle.card());

        VBox wrapper = new VBox(card);
        wrapper.setPadding(new Insets(30));
        return wrapper;
    }

    private HBox createFooter() {
        Label note = new Label("Admin role verified  ·  Status updates are available for Food Requests only.");
        note.setStyle("-fx-text-fill: " + UiStyle.MUTED_TEXT + "; -fx-font-size: 12px;");
        HBox footer = new HBox(note);
        footer.setPadding(new Insets(11, 24, 11, 24));
        footer.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0;"
                + " -fx-border-width: 1 0 0 0;");
        return footer;
    }

    private void configureControls() {
        recordTypeBox.setOnAction(event -> {
            selectionDetails.setText("Select a record to view its details.");
            statusBox.setDisable("Donations".equals(recordTypeBox.getValue()));
            refreshRecords();
        });

        statusBox.setDisable("Donations".equals(recordTypeBox.getValue()));
        recordList.getSelectionModel().selectedIndexProperty().addListener((observable, oldIndex, newIndex) ->
                showSelectedRecordDetails(newIndex.intValue()));
    }

    private void refreshRecords() {
        recordList.getItems().clear();
        selectionDetails.setText("Select a record to view its details.");
        String keyword = searchField.getText();

        if ("Donations".equals(recordTypeBox.getValue())) {
            displayedDonations = dataManager.searchDonations(keyword);
            displayedRequests = new ArrayList<>();
            for (Donation donation : displayedDonations) {
                recordList.getItems().add(donation.toDisplayString());
            }
        } else {
            displayedRequests = dataManager.searchRequests(keyword);
            displayedDonations = new ArrayList<>();
            for (FoodRequest request : displayedRequests) {
                recordList.getItems().add(request.toDisplayString());
            }
        }
    }

    private void showSelectedRecordDetails(int selectedIndex) {
        if (selectedIndex < 0) {
            return;
        }

        if ("Donations".equals(recordTypeBox.getValue())) {
            if (selectedIndex >= displayedDonations.size()) {
                return;
            }
            Donation donation = displayedDonations.get(selectedIndex);
            selectionDetails.setText("Donation ID: " + donation.getDonationId()
                    + "\nDonor: " + donation.getDonorName()
                    + "\nPhone: " + donation.getPhone()
                    + "\nFood: " + donation.getFoodName()
                    + "\nCategory: " + donation.getCategory()
                    + "\nQuantity: " + donation.getQuantity()
                    + "\nExpiry Date: " + donation.getExpiryDate()
                    + "\nPickup Location: " + donation.getPickupLocation()
                    + "\nDate Submitted: " + donation.getDateSubmitted()
                    + "\nStatus: " + donation.getStatus()
                    + "\nNotes: " + donation.getNotes());
        } else {
            if (selectedIndex >= displayedRequests.size()) {
                return;
            }
            FoodRequest request = displayedRequests.get(selectedIndex);
            selectionDetails.setText("Request ID: " + request.getRequestId()
                    + "\nRequester: " + request.getRequesterName()
                    + "\nPhone: " + request.getPhone()
                    + "\nFood Item: " + request.getFoodItem()
                    + "\nQuantity: " + request.getQuantity()
                    + "\nFamily Size: " + request.getFamilySize()
                    + "\nCategory Needed: " + request.getCategoryNeeded()
                    + "\nUrgency: " + request.getUrgency()
                    + "\nDate Submitted: " + request.getDateSubmitted()
                    + "\nStatus: " + request.getStatus()
                    + "\nReason / Notes: " + request.getNotes());
        }
    }

    private void updateSelectedRequestStatus() {
        if ("Donations".equals(recordTypeBox.getValue())) {
            AlertUtil.showWarning("Status Update Not Available",
                    "Only Food Request records have a status.");
            return;
        }

        int selectedIndex = recordList.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= displayedRequests.size()) {
            AlertUtil.showWarning("No Request Selected",
                    "Please select a food request before updating its status.");
            return;
        }

        String newStatus = statusBox.getValue();
        if (newStatus == null || newStatus.isBlank()) {
            AlertUtil.showWarning("No Status Selected",
                    "Please select a new request status.");
            return;
        }

        FoodRequest selectedRequest = displayedRequests.get(selectedIndex);
        boolean updated = dataManager.updateRequestStatus(selectedRequest.getRequestId(), newStatus);
        if (updated) {
            AlertUtil.showInformation("Status Updated",
                    "Request " + selectedRequest.getRequestId() + " was updated to " + newStatus + ".");
            refreshRecords();
        } else {
            AlertUtil.showError("Update Failed", "The selected request could not be found.");
        }
    }

    private void deleteSelectedRecord() {
        int selectedIndex = recordList.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            AlertUtil.showWarning("No Record Selected",
                    "Please select a record before deleting.");
            return;
        }

        boolean confirmed = AlertUtil.showConfirmation("Delete Selected Record",
                "Are you sure you want to permanently delete this record?");
        if (!confirmed) {
            return;
        }

        boolean deleted;
        if ("Donations".equals(recordTypeBox.getValue())) {
            if (selectedIndex >= displayedDonations.size()) {
                return;
            }
            deleted = dataManager.deleteDonation(displayedDonations.get(selectedIndex).getDonationId());
        } else {
            if (selectedIndex >= displayedRequests.size()) {
                return;
            }
            deleted = dataManager.deleteRequest(displayedRequests.get(selectedIndex).getRequestId());
        }

        if (deleted) {
            AlertUtil.showInformation("Record Deleted", "The selected record was deleted successfully.");
            refreshRecords();
        } else {
            AlertUtil.showError("Delete Failed", "The selected record could not be found.");
        }
    }
}
