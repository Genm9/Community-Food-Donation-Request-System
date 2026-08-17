import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;

/**
 * Food assistance request form connected to the shared DataManager.
 */
public class RequestFormView {
    private final DataManager dataManager;
    private final AppNavigator navigator;

    private final TextField requesterNameField = new TextField();
    private final TextField phoneField = new TextField();
    private final TextField familySizeField = new TextField();
    private final ComboBox<String> categoryBox = new ComboBox<>();
    private final ComboBox<String> urgencyBox = new ComboBox<>();
    private final TextArea notesArea = new TextArea();

    public RequestFormView(DataManager dataManager, AppNavigator navigator) {
        this.dataManager = dataManager;
        this.navigator = navigator;
    }

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setStyle(UiStyle.pageBackground());
        root.setTop(createHeader());
        root.setLeft(NavigationMenu.userMenu(navigator, "request"));
        ScrollPane scrollPane = new ScrollPane(createForm());
        scrollPane.setFitToWidth(true); 
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        root.setCenter(scrollPane);
        return root;
    }

    private HBox createHeader() {
        Label title = new Label("Request Food Assistance");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label subtitle = new Label("Tell us what your household needs and we will review your request");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #DBEAFE;");
        VBox heading = new VBox(4, title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label pagePill = new Label("NEW REQUEST");
        pagePill.setStyle("-fx-background-color: rgba(255,255,255,0.16); -fx-text-fill: white;"
                + " -fx-font-weight: bold; -fx-padding: 9 14; -fx-background-radius: 18;");

        HBox header = new HBox(18, heading, spacer, pagePill);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(24, 30, 24, 30));
        header.setStyle(UiStyle.header("#1D4ED8", "#2563EB"));
        return header;
    }

    private VBox createForm() {
        categoryBox.getItems().setAll("Dry Food", "Canned Food", "Fresh Produce", "Beverages", "Baby Food", "Other");
        categoryBox.setPromptText("Select category needed");
        urgencyBox.getItems().setAll("Low", "Medium", "High");
        urgencyBox.setPromptText("Select urgency level");
        notesArea.setPromptText("Optional special request or dietary note");
        notesArea.setPrefRowCount(3);

        UiStyle.styleInput(requesterNameField);
        UiStyle.styleInput(phoneField);
        UiStyle.styleInput(familySizeField);
        UiStyle.styleInput(categoryBox);
        UiStyle.styleInput(urgencyBox);
        UiStyle.styleInput(notesArea);

        GridPane form = new GridPane();
        form.setHgap(18);
        form.setVgap(14);
        form.setMaxWidth(720);
        form.add(formLabel("Requester Name"), 0, 0);
        form.add(requesterNameField, 1, 0);
        form.add(formLabel("Phone Number"), 0, 1);
        form.add(phoneField, 1, 1);
        form.add(formLabel("Family Size"), 0, 2);
        form.add(familySizeField, 1, 2);
        form.add(formLabel("Food Category Needed"), 0, 3);
        form.add(categoryBox, 1, 3);
        form.add(formLabel("Urgency Level"), 0, 4);
        form.add(urgencyBox, 1, 4);
        form.add(formLabel("Notes"), 0, 5);
        form.add(notesArea, 1, 5);

        for (int row = 0; row <= 5; row++) {
            GridPane.setHgrow(form.getChildren().get(row * 2 + 1), Priority.ALWAYS);
        }

        Button submitButton = UiStyle.primaryButton("Submit Request", UiStyle.USER_ACCENT,
                this::submitRequest);
        Button clearButton = UiStyle.secondaryButton("Clear Form", this::clearForm);
        Button backButton = UiStyle.secondaryButton("Back to Dashboard", navigator::showDashboard);
        HBox actions = new HBox(10, submitButton, clearButton, backButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        Label required = new Label("Fields marked with * are required. New requests begin with Pending status.");
        required.setStyle("-fx-text-fill: " + UiStyle.MUTED_TEXT + "; -fx-font-size: 12px;");

        Label heading = UiStyle.pageTitle("Tell us what you need");
        Label intro = UiStyle.pageSubtitle("Provide a few details so the administrator can review your food assistance request.");

        VBox card = new VBox(18, heading, intro, form, actions, required);
        card.setPadding(new Insets(26));
        card.setMaxWidth(820);
        card.setStyle(UiStyle.card());

        VBox wrapper = new VBox(card);
        wrapper.setPadding(new Insets(30));
        return wrapper;
    }

    private Label formLabel(String text) {
        Label label = new Label(text + " *");
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + UiStyle.TEXT + ";");
        return label;
    }

    private void submitRequest() {
        try {
            String requesterName = ValidationUtil.requireText(requesterNameField.getText(), "Requester name");
            String phone = ValidationUtil.validatePhone(phoneField.getText());
            int familySize = ValidationUtil.parsePositiveInteger(familySizeField.getText(), "Family size");
            String category = ValidationUtil.requireText(categoryBox.getValue(), "Food category needed");
            String urgency = ValidationUtil.requireText(urgencyBox.getValue(), "Urgency level");
            String notes = notesArea.getText();

            FoodRequest request = new FoodRequest(dataManager.generateRequestId(), requesterName, phone,
                    familySize, category, urgency, notes, "Pending");
            dataManager.addRequest(request);
            AlertUtil.showInformation("Food Request Submitted",
                    "Request " + request.getRequestId() + " was submitted successfully.");
            clearForm();
        } catch (InvalidDataFormatException | IllegalArgumentException ex) {
            AlertUtil.showWarning("Invalid Food Request Form", ex.getMessage());
        }
    }

    private void clearForm() {
        requesterNameField.clear();
        phoneField.clear();
        familySizeField.clear();
        categoryBox.setValue(null);
        urgencyBox.setValue(null);
        notesArea.clear();
    }
}
