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
 * Food donation form connected to the shared DataManager.
 */
public class DonationFormView {
    private final DataManager dataManager;
    private final AppNavigator navigator;

    private final TextField donorNameField = new TextField();
    private final TextField phoneField = new TextField();
    private final TextField foodNameField = new TextField();
    private final ComboBox<String> categoryBox = new ComboBox<>();
    private final TextField quantityField = new TextField();
    private final TextField expiryDateField = new TextField();
    private final TextArea notesArea = new TextArea();

    public DonationFormView(DataManager dataManager, AppNavigator navigator) {
        this.dataManager = dataManager;
        this.navigator = navigator;
    }

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setStyle(UiStyle.pageBackground());
        root.setTop(createHeader());
        root.setLeft(NavigationMenu.userMenu(navigator, "donate"));
        ScrollPane scrollPane = new ScrollPane(createForm());
        scrollPane.setFitToWidth(true); 
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        root.setCenter(scrollPane);
        return root;
    }

    private HBox createHeader() {
        Label title = new Label("Donate Food");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label subtitle = new Label("Share safe food with people in your community");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #DBEAFE;");
        VBox heading = new VBox(4, title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label pagePill = new Label("NEW DONATION");
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
        categoryBox.setPromptText("Select category");
        notesArea.setPromptText("Optional notes, for example: sealed package");
        notesArea.setPrefRowCount(3);

        UiStyle.styleInput(donorNameField);
        UiStyle.styleInput(phoneField);
        UiStyle.styleInput(foodNameField);
        UiStyle.styleInput(categoryBox);
        UiStyle.styleInput(quantityField);
        UiStyle.styleInput(expiryDateField);
        UiStyle.styleInput(notesArea);

        GridPane form = new GridPane();
        form.setHgap(18);
        form.setVgap(14);
        form.setMaxWidth(720);
        form.add(formLabel("Donor Name"), 0, 0);
        form.add(donorNameField, 1, 0);
        form.add(formLabel("Phone Number"), 0, 1);
        form.add(phoneField, 1, 1);
        form.add(formLabel("Food Name"), 0, 2);
        form.add(foodNameField, 1, 2);
        form.add(formLabel("Food Category"), 0, 3);
        form.add(categoryBox, 1, 3);
        form.add(formLabel("Quantity"), 0, 4);
        form.add(quantityField, 1, 4);
        form.add(formLabel("Expiry Date"), 0, 5);
        form.add(expiryDateField, 1, 5);
        form.add(formLabel("Notes"), 0, 6);
        form.add(notesArea, 1, 6);

        for (int row = 0; row <= 6; row++) {
            GridPane.setHgrow(form.getChildren().get(row * 2 + 1), Priority.ALWAYS);
        }

        Button submitButton = UiStyle.primaryButton("Submit Donation", UiStyle.USER_ACCENT,
                this::submitDonation);
        Button clearButton = UiStyle.secondaryButton("Clear Form", this::clearForm);
        Button backButton = UiStyle.secondaryButton("Back to Dashboard", navigator::showDashboard);
        HBox actions = new HBox(10, submitButton, clearButton, backButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        Label required = new Label("Fields marked with * are required. Your donation will be stored locally.");
        required.setStyle("-fx-text-fill: " + UiStyle.MUTED_TEXT + "; -fx-font-size: 12px;");

        Label heading = UiStyle.pageTitle("Tell us what you can share");
        Label intro = UiStyle.pageSubtitle("Enter the food details below so the community can understand the available donation.");

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

    private void submitDonation() {
        try {
            String donorName = ValidationUtil.requireText(donorNameField.getText(), "Donor name");
            String phone = ValidationUtil.validatePhone(phoneField.getText());
            String foodName = ValidationUtil.requireText(foodNameField.getText(), "Food name");
            String category = ValidationUtil.requireText(categoryBox.getValue(), "Food category");
            int quantity = ValidationUtil.parsePositiveInteger(quantityField.getText(), "Quantity");
            String expiryDate = ValidationUtil.requireText(expiryDateField.getText(), "Expiry date");
            String notes = notesArea.getText();

            Donation donation = new Donation(dataManager.generateDonationId(), donorName, phone,
                    foodName, category, quantity, expiryDate, notes);
            dataManager.addDonation(donation);
            AlertUtil.showInformation("Donation Submitted",
                    "Donation " + donation.getDonationId() + " was submitted successfully.");
            clearForm();
        } catch (InvalidDataFormatException | IllegalArgumentException ex) {
            AlertUtil.showWarning("Invalid Donation Form", ex.getMessage());
        }
    }

    private void clearForm() {
        donorNameField.clear();
        phoneField.clear();
        foodNameField.clear();
        categoryBox.setValue(null);
        quantityField.clear();
        expiryDateField.clear();
        notesArea.clear();
    }
}
