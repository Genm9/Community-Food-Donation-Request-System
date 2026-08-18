import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

/**
 * Integrated request module.
 *
 * The layout follows the teammate's RequestFormPanel: a request form on the
 * left and the logged-in user's submitted-request table on the right. The
 * Swing controls are represented with JavaFX controls so the module can live
 * inside the existing application navigator and shared DataManager.
 */
public class RequestFormView {
    private static final String PHONE_NOT_PROVIDED = "N/A";

    private final DataManager dataManager;
    private final AppNavigator navigator;

    private final TextField foodItemField = new TextField();
    private final TextField quantityField = new TextField();
    private final TextArea reasonArea = new TextArea();
    private final RadioButton lowRadio = new RadioButton("Low");
    private final RadioButton mediumRadio = new RadioButton("Medium");
    private final RadioButton highRadio = new RadioButton("High");
    private final TableView<FoodRequest> requestTable = new TableView<>();

    public RequestFormView(DataManager dataManager, AppNavigator navigator) {
        this.dataManager = dataManager;
        this.navigator = navigator;
    }

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setStyle(UiStyle.pageBackground());
        root.setTop(createHeader());
        root.setLeft(NavigationMenu.userMenu(navigator, "request"));

        ScrollPane scrollPane = new ScrollPane(createContent());
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;"
                + " -fx-border-color: transparent;");
        root.setCenter(scrollPane);

        refreshTable();
        return root;
    }

    private HBox createHeader() {
        Label title = new Label("Request Food Assistance");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label subtitle = new Label("Submit a request and review your assistance history");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #DBEAFE;");
        VBox heading = new VBox(4, title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label pagePill = new Label("REQUEST FOOD");
        pagePill.setStyle("-fx-background-color: rgba(255,255,255,0.16); -fx-text-fill: white;"
                + " -fx-font-weight: bold; -fx-padding: 9 14; -fx-background-radius: 18;");

        HBox header = new HBox(18, heading, spacer, pagePill);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(24, 30, 24, 30));
        header.setStyle(UiStyle.header("#1D4ED8", "#2563EB"));
        return header;
    }

    private VBox createContent() {
        configureInputs();

        HBox moduleContent = new HBox(20, buildFormPanel(), buildTablePanel());
        moduleContent.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(moduleContent.getChildren().get(1), Priority.ALWAYS);

        Label pageTitle = UiStyle.pageTitle("Request Food Assistance");
        Label pageSubtitle = UiStyle.pageSubtitle(
                "Tell us what food your household needs and choose the urgency level for review.");

        VBox card = new VBox(16, pageTitle, pageSubtitle, moduleContent);
        card.setPadding(new Insets(26));
        card.setStyle(UiStyle.card());

        VBox wrapper = new VBox(card);
        wrapper.setPadding(new Insets(30));
        return wrapper;
    }

    private void configureInputs() {
        reasonArea.setPromptText("Explain why you need this food assistance");
        reasonArea.setWrapText(true);
        reasonArea.setPrefRowCount(4);
        reasonArea.setMinHeight(100);

        UiStyle.styleInput(foodItemField);
        UiStyle.styleInput(quantityField);
        UiStyle.styleInput(reasonArea);

        ToggleGroup urgencyGroup = new ToggleGroup();
        lowRadio.setToggleGroup(urgencyGroup);
        mediumRadio.setToggleGroup(urgencyGroup);
        highRadio.setToggleGroup(urgencyGroup);
        mediumRadio.setSelected(true);

        for (RadioButton radio : new RadioButton[]{lowRadio, mediumRadio, highRadio}) {
            radio.setStyle("-fx-text-fill: " + UiStyle.TEXT + "; -fx-font-size: 13px;");
        }
    }

    private VBox buildFormPanel() {
        VBox form = new VBox(10);
        form.setPadding(new Insets(20));
        form.setPrefWidth(400);
        form.setMinWidth(330);
        form.setStyle(UiStyle.card());

        Label formTitle = sectionBar("  SUBMIT A FOOD REQUEST", "#1E40AF");
        form.getChildren().add(formTitle);

        form.getChildren().addAll(
                fieldLabel("Food item"), foodItemField,
                fieldLabel("Quantity"), quantityField,
                fieldLabel("Reason"), reasonArea,
                fieldLabel("Urgency"), urgencyRow());

        Button submitButton = UiStyle.primaryButton("Submit Request", UiStyle.USER_ACCENT,
                this::submitRequest);
        Button clearButton = UiStyle.secondaryButton("Clear Form", this::clearForm);
        submitButton.setMaxWidth(Double.MAX_VALUE);
        clearButton.setMaxWidth(Double.MAX_VALUE);

        form.getChildren().addAll(submitButton, clearButton);
        VBox.setMargin(submitButton, new Insets(10, 0, 0, 0));
        return form;
    }

    private HBox urgencyRow() {
        HBox row = new HBox(14, lowRadio, mediumRadio, highRadio);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(3, 0, 8, 0));
        return row;
    }

    private VBox buildTablePanel() {
        VBox panel = new VBox();
        panel.setMinWidth(500);
        panel.setPrefWidth(700);
        panel.setStyle(UiStyle.card());

        Label tableTitle = sectionBar("  MY SUBMITTED REQUESTS", "#1E40AF");
        panel.getChildren().add(tableTitle);

        requestTable.setPlaceholder(new Label("No requests submitted yet."));
        requestTable.setPrefHeight(330);
        requestTable.setMinHeight(260);
        requestTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        requestTable.setStyle("-fx-background-color: white; -fx-border-color: transparent;"
                + " -fx-font-size: 13px;");
        requestTable.setRowFactory(table -> {
            TableRow<FoodRequest> row = new TableRow<>();
            Runnable restyle = () -> styleHistoryRow(row);
            row.itemProperty().addListener((observable, oldValue, newValue) -> restyle.run());
            row.indexProperty().addListener((observable, oldValue, newValue) -> restyle.run());
            row.selectedProperty().addListener((observable, oldValue, newValue) -> restyle.run());
            return row;
        });

        addColumn("Food Item", 150, request -> request.getFoodItem());
        addColumn("Qty", 70, request -> String.valueOf(request.getQuantity()));
        addColumn("Urgency", 100, FoodRequest::getUrgency);
        addColumn("Date", 110, request -> request.getDateSubmitted().isBlank()
                ? "-" : request.getDateSubmitted());
        addColumn("Status", 100, FoodRequest::getStatus);

        VBox.setVgrow(requestTable, Priority.ALWAYS);
        panel.getChildren().add(requestTable);
        return panel;
    }

    private void addColumn(String title, double width,
                           Function<FoodRequest, String> valueProvider) {
	TableColumn<FoodRequest, String> column = new TableColumn<>();
	Label headerLabel = new Label(title);
    	headerLabel.setStyle("-fx-text-fill: " + UiStyle.TEXT + "; -fx-font-weight: bold;");
    	column.setGraphic(headerLabel);
        column.setMinWidth(width);
        column.setCellValueFactory(cellData ->
                new SimpleStringProperty(valueProvider.apply(cellData.getValue())));
        requestTable.getColumns().add(column);
    }

    private Label sectionBar(String text, String colour) {
        Label sectionTitle = new Label(text);
        sectionTitle.setMaxWidth(Double.MAX_VALUE);
        sectionTitle.setPadding(new Insets(9, 8, 9, 8));
        sectionTitle.setStyle("-fx-background-color: " + colour + "; -fx-text-fill: white;"
                + " -fx-font-size: 14px; -fx-font-weight: bold;");
        return sectionTitle;
    }

    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: "
                + UiStyle.TEXT + ";");
        return label;
    }

    private void styleHistoryRow(TableRow<FoodRequest> row) {
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

    private void submitRequest() {
        try {
            String foodItem = ValidationUtil.requireText(foodItemField.getText(), "Food item");
            int quantity = ValidationUtil.parsePositiveInteger(quantityField.getText(), "Quantity");
            String reason = ValidationUtil.requireText(reasonArea.getText(), "Reason");
            if (reason.contains(",")) {
                throw new InvalidDataFormatException("Reason cannot contain commas.");
            }

            String urgency = getSelectedUrgency();
            String username = navigator.getLoggedInUsername();
            if (username == null || username.isBlank()) {
                username = "Unknown User";
            }

            FoodRequest request = new FoodRequest(
                    dataManager.generateRequestId(),
                    username,
                    PHONE_NOT_PROVIDED,
                    1,
                    "Food Assistance",
                    urgency,
                    reason,
                    "Pending",
                    foodItem,
                    quantity,
                    LocalDate.now().toString());
            dataManager.addRequest(request);
            navigator.saveData();
            refreshTable();
            clearForm();
            AlertUtil.showInformation("Request Submitted",
                    "Request " + request.getRequestId() + " was submitted successfully.");
        } catch (InvalidDataFormatException | IllegalArgumentException ex) {
            AlertUtil.showWarning("Invalid Request Form", ex.getMessage());
        }
    }

    private String getSelectedUrgency() {
        if (lowRadio.isSelected()) {
            return "Low";
        }
        if (highRadio.isSelected()) {
            return "High";
        }
        return "Medium";
    }

    private void clearForm() {
        foodItemField.clear();
        quantityField.clear();
        reasonArea.clear();
        mediumRadio.setSelected(true);
        foodItemField.requestFocus();
    }

    private void refreshTable() {
        String username = navigator.getLoggedInUsername();
        List<FoodRequest> myRequests = dataManager.getRequests().stream()
                .filter(request -> username != null
                        && username.equalsIgnoreCase(request.getRequesterName()))
                .toList();
        requestTable.getItems().setAll(myRequests);
    }
}
