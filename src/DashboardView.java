import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Modern user dashboard with an active Dashboard taskbar item.
 */
public class DashboardView {
    private final DataManager dataManager;
    private final AppNavigator navigator;

    public DashboardView(DataManager dataManager, AppNavigator navigator) {
        this.dataManager = dataManager;
        this.navigator = navigator;
    }

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setStyle(UiStyle.pageBackground());
        root.setTop(createHeader());
        root.setLeft(NavigationMenu.userMenu(navigator, "dashboard"));
        root.setCenter(createContent());
        root.setBottom(createStatusBar());
        return root;
    }

    private HBox createHeader() {
        Label eyebrow = new Label("COMMUNITY FOOD");
        eyebrow.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #BFDBFE;"
                + " -fx-letter-spacing: 1.4px;");

        Label title = new Label("Donation & Request System");
        title.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("A simple way to share food and support local families");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #DBEAFE;");

        VBox heading = new VBox(3, eyebrow, title, subtitle);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userPill = new Label("USER  ·  " + navigator.getLoggedInUsername());
        userPill.setStyle("-fx-background-color: rgba(255,255,255,0.16); -fx-text-fill: white;"
                + " -fx-font-weight: bold; -fx-padding: 9 14; -fx-background-radius: 18;");

        HBox header = new HBox(18, heading, spacer, userPill);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(24, 30, 24, 30));
        header.setStyle(UiStyle.header("#1D4ED8", "#2563EB"));
        return header;
    }

    private VBox createContent() {
        Label welcome = UiStyle.pageTitle("Welcome back, " + navigator.getLoggedInUsername() + "");
        Label instruction = UiStyle.pageSubtitle(
                "Use the quick actions to donate food, request assistance, or review the latest community records.");

        HBox cards = new HBox(16,
                createMetricCard("Total Donations", String.valueOf(dataManager.getDonationCount()),
                        UiStyle.USER_ACCENT, "Food shared"),
                createMetricCard("Total Requests", String.valueOf(dataManager.getRequestCount()),
                        "#7C3AED", "Requests received"),
                createMetricCard("Pending Requests", String.valueOf(dataManager.getPendingRequestCount()),
                        UiStyle.WARNING, "Awaiting review"));
        cards.setPadding(new Insets(22, 0, 22, 0));
        HBox.setHgrow(cards.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(cards.getChildren().get(1), Priority.ALWAYS);
        HBox.setHgrow(cards.getChildren().get(2), Priority.ALWAYS);

        Label storageHeading = UiStyle.sectionTitle("Quick overview");
        Label storageText = UiStyle.pageSubtitle(
                "Your records are stored locally in text files. Use Save Data after making changes, "
                        + "or Load Data to retrieve the latest saved records.");

        HBox contentCard = new HBox(storageHeading, storageText);
        contentCard.setSpacing(18);
        contentCard.setAlignment(Pos.CENTER_LEFT);
        contentCard.setPadding(new Insets(20));
        contentCard.setStyle(UiStyle.card());

        VBox content = new VBox(7, welcome, instruction, cards, contentCard);
        content.setPadding(new Insets(32));
        return content;
    }

    private VBox createMetricCard(String title, String value, String accent, String caption) {
        Label titleLabel = new Label(title.toUpperCase());
        titleLabel.setStyle("-fx-text-fill: " + UiStyle.MUTED_TEXT + "; -fx-font-size: 11px;"
                + " -fx-font-weight: bold; -fx-letter-spacing: 0.8px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + accent + "; -fx-font-size: 30px; -fx-font-weight: bold;");

        Label captionLabel = new Label(caption);
        captionLabel.setStyle("-fx-text-fill: " + UiStyle.MUTED_TEXT + "; -fx-font-size: 12px;");

        VBox card = new VBox(7, titleLabel, valueLabel, captionLabel);
        card.setPadding(new Insets(20));
        card.setMinHeight(130);
        card.setStyle(UiStyle.card());
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private HBox createStatusBar() {
        Label status = new Label("●  " + navigator.getStatusMessage());
        status.setStyle("-fx-text-fill: " + UiStyle.MUTED_TEXT + "; -fx-font-size: 12px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label savedFiles = new Label("Local storage  ·  donations.txt  ·  requests.txt  ·  accounts.txt");
        savedFiles.setStyle("-fx-text-fill: " + UiStyle.MUTED_TEXT + "; -fx-font-size: 12px;");

        HBox statusBar = new HBox(10, status, spacer, savedFiles);
        statusBar.setPadding(new Insets(11, 24, 11, 24));
        statusBar.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0;"
                + " -fx-border-width: 1 0 0 0;");
        return statusBar;
    }
}
