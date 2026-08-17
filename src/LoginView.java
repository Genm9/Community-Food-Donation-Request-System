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
 * Modern login screen for both normal users and administrators.
 */
public class LoginView {
    private final DataManager dataManager;
    private final AppNavigator navigator;

    public LoginView(DataManager dataManager, AppNavigator navigator) {
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
        shell.setMaxHeight(540);
        shell.setStyle(UiStyle.card());

        root.getChildren().add(shell);
        return root;
    }

    private VBox createBrandingPanel() {
        Label eyebrow = new Label("COMMUNITY SUPPORT");
        eyebrow.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #BFDBFE;"
                + " -fx-letter-spacing: 1.4px;");

        Label title = new Label("Food shared\nwith care.");
        title.setWrapText(true);
        title.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: white;"
                + " -fx-line-spacing: 5px;");

        Label description = new Label("Connect food donors with families who need support."
                + " Every record helps the community respond with clarity and care.");
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 14px; -fx-text-fill: #DBEAFE; -fx-line-spacing: 3px;");

        Label status = new Label("LOCAL COMMUNITY PLATFORM");
        status.setStyle("-fx-background-color: rgba(255,255,255,0.16); -fx-text-fill: white;"
                + " -fx-font-weight: bold; -fx-padding: 9 12; -fx-background-radius: 18;");

        VBox branding = new VBox(18, eyebrow, title, description, status);
        branding.setAlignment(Pos.TOP_LEFT);
        branding.setPadding(new Insets(44, 38, 40, 38));
        branding.setPrefWidth(370);
        branding.setStyle(UiStyle.header("#1D4ED8", "#2563EB") + " -fx-background-radius: 14 0 0 14;");
        return branding;
    }

    private VBox createFormPanel() {
        Label title = UiStyle.pageTitle("Welcome back");
        Label subtitle = UiStyle.pageSubtitle("Sign in to continue to your community workspace.");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        UiStyle.styleInput(usernameField);
        usernameField.setMaxWidth(Double.MAX_VALUE);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        UiStyle.styleInput(passwordField);
        passwordField.setMaxWidth(Double.MAX_VALUE);

        Label message = new Label("Use your User or Admin account. Your role determines the dashboard you see.");
        message.setWrapText(true);
        message.setStyle("-fx-text-fill: " + UiStyle.MUTED_TEXT + "; -fx-font-size: 12px;");

        Button loginButton = UiStyle.primaryButton("Login", UiStyle.USER_ACCENT, () -> {
            try {
                String username = ValidationUtil.requireText(usernameField.getText(), "Username");
                String password = ValidationUtil.requireText(passwordField.getText(), "Password");
                UserAccount account = dataManager.authenticate(username, password);

                if (account == null) {
                    AlertUtil.showWarning("Login Failed",
                            "Incorrect username or password. Please try again.");
                    return;
                }

                navigator.completeLogin(account.getUsername(), account.getRole());
            } catch (InvalidDataFormatException ex) {
                AlertUtil.showWarning("Incomplete Login", ex.getMessage());
            }
        });
        loginButton.setDefaultButton(true);

        Button registerButton = UiStyle.secondaryButton("Create a New Account", navigator::showRegister);
        registerButton.setMaxWidth(Double.MAX_VALUE);

        Label footer = new Label("New to the platform? Create a free User account to donate or request food.");
        footer.setWrapText(true);
        footer.setStyle("-fx-text-fill: " + UiStyle.MUTED_TEXT + "; -fx-font-size: 12px;");

        VBox form = new VBox(14, title, subtitle, usernameField, passwordField,
                message, loginButton, registerButton, footer);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setPadding(new Insets(44, 42, 40, 42));
        form.setPrefWidth(530);
        HBox.setHgrow(form, Priority.ALWAYS);
        return form;
    }
}
