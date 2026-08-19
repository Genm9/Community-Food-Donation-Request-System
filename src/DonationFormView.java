import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;


public class DonationFormView {
    private static final String[] CATEGORIES = {
            "Grains & Rice", "Canned Goods", "Vegetables", "Fruits",
            "Dairy", "Beverages", "Other"
    };
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String PHONE_NOT_PROVIDED = "N/A";

    private final DataManager dataManager;
    private final AppNavigator navigator;

    private final TextField foodItemField = new TextField();
    private final TextField quantityField = new TextField();
    private final javafx.scene.control.ComboBox<String> categoryBox =
            new javafx.scene.control.ComboBox<>();
    private final DatePicker expiryDatePicker = new DatePicker();
    private final TextField pickupLocationField = new TextField();
    private final ObservableList<Donation> historyItems = FXCollections.observableArrayList();
    private TableView<Donation> historyTable;

    public DonationFormView(DataManager dataManager, AppNavigator navigator) {
        this.dataManager = dataManager;
        this.navigator = navigator;
    }

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setStyle(UiStyle.pageBackground());
        root.setTop(createHeader());
        root.setLeft(NavigationMenu.userMenu(navigator, "donate"));

        VBox body = new VBox(14, createFormPanel(), createHistoryPanel());
        body.setPadding(new Insets(18));
        body.setStyle(UiStyle.pageBackground());

        ScrollPane scrollPane = new ScrollPane(body);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;"
                + " -fx-border-color: transparent;");
        root.setCenter(scrollPane);

        refreshHistory();
        return root;
    }

    private HBox createHeader() {
        Label foodLabel = new Label("FOOD");
        foodLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label donateLabel = new Label(" DONATE");
        donateLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #DBEAFE;");

        HBox title = new HBox(foodLabel, donateLabel);
        title.setAlignment(Pos.CENTER_LEFT);

        Label subtitle = new Label("Community Food Donation Portal");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #DBEAFE;");
        VBox heading = new VBox(3, title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label pagePill = new Label("DONATE FOOD");
        pagePill.setStyle("-fx-background-color: rgba(255,255,255,0.16); -fx-text-fill: white;"
                + " -fx-font-weight: bold; -fx-padding: 9 14; -fx-background-radius: 18;");

        HBox header = new HBox(18, heading, spacer, pagePill);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 20, 16, 20));
        header.setStyle(UiStyle.header("#1D4ED8", "#2563EB"));
        return header;
    }

    private VBox createFormPanel() {
        VBox outer = new VBox();
        outer.setStyle(UiStyle.card());

        Label sectionTitle = sectionBar("  SUBMIT A FOOD DONATION", UiStyle.USER_ACCENT);
        outer.getChildren().add(sectionTitle);

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(8);
        form.setPadding(new Insets(16, 20, 12, 20));
        form.setStyle("-fx-background-color: " + UiStyle.CARD_BACKGROUND + ";");

        ColumnConstraints labelColumn = new ColumnConstraints(150);
        ColumnConstraints inputColumn = new ColumnConstraints();
        inputColumn.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(labelColumn, inputColumn);

        categoryBox.getItems().setAll(CATEGORIES);
        categoryBox.setValue(CATEGORIES[0]);
        categoryBox.setMaxWidth(Double.MAX_VALUE);
        expiryDatePicker.setValue(defaultExpiryDate());
        expiryDatePicker.setEditable(false);
        expiryDatePicker.setMaxWidth(Double.MAX_VALUE);

        UiStyle.styleInput(foodItemField);
        UiStyle.styleInput(quantityField);
        UiStyle.styleInput(categoryBox);
        UiStyle.styleInput(expiryDatePicker);
        UiStyle.styleInput(pickupLocationField);

        addFormRow(form, "Food Item:", foodItemField, 0);
        addFormRow(form, "Quantity:", quantityField, 1);
        addFormRow(form, "Category:", categoryBox, 2);
        addFormRow(form, "Expiry Date:", expiryDatePicker, 3);
        addFormRow(form, "Pickup Location:", pickupLocationField, 4);

        Button clearButton = UiStyle.secondaryButton("Clear", this::clearForm);
        Button submitButton = UiStyle.primaryButton("Submit Donation", UiStyle.USER_ACCENT, this::submitDonation);
        clearButton.setMaxWidth(180);
        submitButton.setMaxWidth(220);

        HBox buttonPanel = new HBox(10, clearButton, submitButton);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);
        buttonPanel.setPadding(new Insets(8, 20, 16, 20));
        form.add(buttonPanel, 0, 5, 2, 1);

        outer.getChildren().add(form);
        return outer;
    }

    private void addFormRow(GridPane form, String labelText, javafx.scene.Node control, int row) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + UiStyle.TEXT + ";");
        form.add(label, 0, row);
        form.add(control, 1, row);
        GridPane.setHgrow(control, Priority.ALWAYS);
    }

    private VBox createHistoryPanel() {
        VBox panel = new VBox();
        panel.setStyle(UiStyle.card());

        Label sectionTitle = sectionBar("  MY DONATION HISTORY", "#1E40AF");
        panel.getChildren().add(sectionTitle);

        historyTable = new TableView<>(historyItems);
        historyTable.setPlaceholder(new Label("No donations submitted yet."));
        historyTable.setPrefHeight(300);
        historyTable.setMinHeight(250);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.setStyle("-fx-background-color: white; -fx-border-color: transparent;"
                + " -fx-font-size: 13px;");
        historyTable.setRowFactory(table -> {
            TableRow<Donation> row = new TableRow<>();
            Runnable restyle = () -> styleHistoryRow(row);
            row.itemProperty().addListener((observable, oldValue, newValue) -> restyle.run());
            row.indexProperty().addListener((observable, oldValue, newValue) -> restyle.run());
            row.selectedProperty().addListener((observable, oldValue, newValue) -> restyle.run());
            return row;
        });

        addColumn("ID", 75, donation -> donation.getDonationId());
        addColumn("Food Item", 150, donation -> donation.getFoodName());
        addColumn("Quantity", 80, donation -> String.valueOf(donation.getQuantity()));
        addColumn("Category", 130, donation -> donation.getCategory());
        addColumn("Expiry Date", 110, donation -> donation.getExpiryDate());
        addColumn("Pickup Location", 150, donation -> donation.getPickupLocation());
        addColumn("Date Submitted", 120, donation -> donation.getDateSubmitted());
        addColumn("Status", 90, donation -> donation.getStatus());

        VBox.setVgrow(historyTable, Priority.ALWAYS);
        panel.getChildren().add(historyTable);
        return panel;
    }

    private void addColumn(String title, double width,
                           java.util.function.Function<Donation, String> valueProvider) {
        TableColumn<Donation, String> column = new TableColumn<>();
	Label headerLabel = new Label(title);
    	headerLabel.setStyle("-fx-text-fill: " + UiStyle.TEXT + "; -fx-font-weight: bold;");
    	column.setGraphic(headerLabel);
        column.setMinWidth(width);
        column.setCellValueFactory(cellData ->
                new SimpleStringProperty(valueProvider.apply(cellData.getValue())));
        historyTable.getColumns().add(column);
    }

    private Label sectionBar(String text, String colour) {
        Label sectionTitle = new Label(text);
        sectionTitle.setMaxWidth(Double.MAX_VALUE);
        sectionTitle.setPadding(new Insets(9, 8, 9, 8));
        sectionTitle.setStyle("-fx-background-color: " + colour + "; -fx-text-fill: white;"
                + " -fx-font-size: 14px; -fx-font-weight: bold;");
        return sectionTitle;
    }

    private void styleHistoryRow(TableRow<Donation> row) {
        if (row.isEmpty()) {
            row.setStyle("");
        } else if (row.isSelected()) {
            row.setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: " + UiStyle.TEXT + ";");
        } else {
            row.setStyle(row.getIndex() % 2 == 0
                    ? "-fx-background-color: #FFFFFF;"
                    : "-fx-background-color: #F8FAFC;");
        }
    }

    private void submitDonation() {
        try {
            String foodItem = ValidationUtil.requireText(foodItemField.getText(), "Food item");
            int quantity = ValidationUtil.parsePositiveInteger(quantityField.getText(), "Quantity");
            String category = ValidationUtil.requireText(categoryBox.getValue(), "Category");
            String pickupLocation = ValidationUtil.requireText(pickupLocationField.getText(), "Pickup location");
            LocalDate expiryDate = expiryDatePicker.getValue();
            if (expiryDate == null) {
                throw new InvalidDataFormatException("Expiry date is required.");
            }
            if (expiryDate.isBefore(LocalDate.now())) {
                throw new InvalidDataFormatException("Expiry date cannot be in the past.");
            }

            Donation donation = new Donation(
                    dataManager.generateDonationId(),
                    navigator.getLoggedInUsername(),
                    PHONE_NOT_PROVIDED,
                    foodItem,
                    category,
                    quantity,
                    DATE_FORMAT.format(expiryDate),
                    "",
                    pickupLocation,
                    DATE_FORMAT.format(LocalDate.now()),
                    "PENDING");
            dataManager.addDonation(donation);
            refreshHistory();
            clearForm();
            AlertUtil.showInformation("Donation Submitted",
                    "Donation " + donation.getDonationId() + " was submitted successfully.");
        } catch (InvalidDataFormatException | IllegalArgumentException ex) {
            AlertUtil.showWarning("Invalid Donation Form", ex.getMessage());
        }
    }

    private void clearForm() {
        foodItemField.clear();
        quantityField.clear();
        categoryBox.setValue(CATEGORIES[0]);
        expiryDatePicker.setValue(defaultExpiryDate());
        pickupLocationField.clear();
        foodItemField.requestFocus();
    }

    private LocalDate defaultExpiryDate() {
        return LocalDate.now().plusDays(3);
    }

    private void refreshHistory() {
        if (historyTable == null) {
            return;
        }
        String username = navigator.getLoggedInUsername();
        List<Donation> myDonations = dataManager.getDonations().stream()
                .filter(donation -> username != null
                        && username.equalsIgnoreCase(donation.getDonorName()))
                .toList();
        historyItems.setAll(myDonations);
    }
}
