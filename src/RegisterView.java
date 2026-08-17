import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Modern registration screen. New accounts are always normal User accounts.
 */
public class RegisterView {
    private final DataManager dataManager;
    private final AppNavigator navigator;

    public RegisterView(DataManager dataManager, AppNavigator navigator) {
        this.dataManager = dataManager;
        this.navigator = navigator;
    }

    public Parent build() {
        StackPane root = new StackPane();
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #DBEAFE, #F8FAFC);");

        VBox branding = createBrandingPanel();
        VBox form = createFormPanel();
        HBox shell = new HBox(0, branding, form);
        shell.setMaxWidth(900);
        shell.setMaxHeight(580);
        shell.setStyle(UiStyle.card());

        root.getChildren().add(shell);
        return root;
    }

    private VBox createBrandingPanel() {
        Label eyebrow = new Label("JOIN THE COMMUNITY");
        eyebrow.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #BFDBFE;"
                + " -fx-letter-spacing: 1.4px;");

        Label title = new Label("Small actions.\nReal support.");
        title.setWrapText(true);
        title.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-line-spacing: 5px;");

        Label description = new Label("Create a User account to share a donation, request food assistance, "
                + "and view the records that help our community stay connected.");
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 14px; -fx-text-fill: #DBEAFE; -fx-line-spacing: 3px;");

        Label role = new Label("NEW ACCOUNTS ARE USER ACCOUNTS");
        role.setStyle("-fx-background-color: rgba(255,255,255,0.16); -fx-text-fill: white;"
                + " -fx-font-weight: bold; -fx-padding: 9 12; -fx-background-radius: 18;");

        VBox branding = new VBox(18, eyebrow, title, description, role);
        branding.setAlignment(Pos.TOP_LEFT);
        branding.setPadding(new Insets(44, 38, 40, 38));
        branding.setPrefWidth(370);
        branding.setStyle(UiStyle.header("#1D4ED8", "#2563EB") + " -fx-background-radius: 14 0 0 14;");
        return branding;
    }

    private VBox createFormPanel() {
        Label title = UiStyle.pageTitle("Create your account");
        Label subtitle = UiStyle.pageSubtitle("Register as a community User in a few simple steps.");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a username");
        UiStyle.styleInput(usernameField);
        usernameField.setMaxWidth(Double.MAX_VALUE);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Create a password");
        UiStyle.styleInput(passwordField);
        passwordField.setMaxWidth(Double.MAX_VALUE);

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm your password");
        UiStyle.styleInput(confirmPasswordField);
        confirmPasswordField.setMaxWidth(Double.MAX_VALUE);

        Label message = new Label("Your password must contain at least 6 characters. Admin accounts are created by the system.");
        message.setWrapText(true);
        message.setStyle("-fx-text-fill: " + UiStyle.MUTED_TEXT + "; -fx-font-size: 12px;");

        Button registerButton = UiStyle.primaryButton("Create User Account", UiStyle.USER_ACCENT, () -> {
            try {
                String username = ValidationUtil.requireText(usernameField.getText(), "Username");
                String password = ValidationUtil.requireText(passwordField.getText(), "Password");
                String confirmation = ValidationUtil.requireText(
                        confirmPasswordField.getText(), "Password confirmation");

                if (username.length() < 3) {
                    throw new InvalidDataFormatException("Username must contain at least 3 characters.");
                }
                if (password.length() < 6) {
                    throw new InvalidDataFormatException("Password must contain at least 6 characters.");
                }
                if (!password.equals(confirmation)) {
                    throw new InvalidDataFormatException("Password and confirmation password do not match.");
                }
                if (dataManager.usernameExists(username)) {
                    throw new InvalidDataFormatException("This username is already registered.");
                }

                dataManager.registerAccount(username, password);
                dataManager.saveAllData();
                AlertUtil.showInformation("Registration Successful",
                        "Your User account has been created. Please log in.");
                navigator.showLogin();
            } catch (InvalidDataFormatException ex) {
                AlertUtil.showWarning("Registration Failed", ex.getMessage());
            } catch (java.io.IOException ex) {
                AlertUtil.showError("Storage Error",
                        "The account could not be saved. Please try again.\n\n" + ex.getMessage());
            }
        });

        Button backButton = UiStyle.secondaryButton("Back to Login", navigator::showLogin);
        backButton.setMaxWidth(Double.MAX_VALUE);

        VBox form = new VBox(14, title, subtitle, usernameField, passwordField,
                confirmPasswordField, message, registerButton, backButton);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setPadding(new Insets(44, 42, 40, 42));
        form.setPrefWidth(530);
        HBox.setHgrow(form, Priority.ALWAYS);
        return form;
    }
}
