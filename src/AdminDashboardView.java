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
 * Modern administrator dashboard with role-specific navigation.
 */
public class AdminDashboardView {
    private final DataManager dataManager;
    private final AppNavigator navigator;

    public AdminDashboardView(DataManager dataManager, AppNavigator navigator) {
        this.dataManager = dataManager;
        this.navigator = navigator;
    }

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setStyle(UiStyle.pageBackground());
        root.setTop(createHeader());
        root.setLeft(NavigationMenu.adminMenu(navigator, "admin-dashboard"));
        root.setCenter(createContent());
        root.setBottom(createStatusBar());
        return root;
    }

    private HBox createHeader() {
        Label eyebrow = new Label("CONTROL CENTRE");
        eyebrow.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #CBD5E1;"
                + " -fx-letter-spacing: 1.4px;");

        Label title = new Label("Administrator Dashboard");
        title.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("Review community activity and keep food assistance moving");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #E2E8F0;");

        VBox heading = new VBox(3, eyebrow, title, subtitle);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label rolePill = new Label("ADMIN  ·  " + navigator.getLoggedInUsername());
        rolePill.setStyle("-fx-background-color: rgba(255,255,255,0.16); -fx-text-fill: white;"
                + " -fx-font-weight: bold; -fx-padding: 9 14; -fx-background-radius: 18;");

        HBox header = new HBox(18, heading, spacer, rolePill);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(24, 30, 24, 30));
        header.setStyle(UiStyle.header("#334155", "#475569"));
        return header;
    }

    private VBox createContent() {
        Label welcome = UiStyle.pageTitle("Good day, Administrator");
        Label instruction = UiStyle.pageSubtitle(
                "Review submitted requests, update statuses, and monitor the community food support activity.");

        HBox cards = new HBox(14,
                createMetricCard("Pending Requests", String.valueOf(dataManager.getPendingRequestCount()),
                        UiStyle.WARNING, "Need review"),
                createMetricCard("Approved Requests", String.valueOf(dataManager.getApprovedRequestCount()),
                        UiStyle.SUCCESS, "Ready to support"),
                createMetricCard("Total Donations", String.valueOf(dataManager.getDonationCount()),
                        UiStyle.USER_ACCENT, "Food available"),
                createMetricCard("Total Requests", String.valueOf(dataManager.getRequestCount()),
                        "#7C3AED", "Community demand"));
        cards.setPadding(new Insets(22, 0, 22, 0));
        for (javafx.scene.Node card : cards.getChildren()) {
            HBox.setHgrow(card, Priority.ALWAYS);
        }

        Label heading = UiStyle.sectionTitle("Administrator workspace");
        Label description = UiStyle.pageSubtitle(
                "Use Manage Records to search donations and food requests. You can update a request to "
                        + "Pending, Approved, Fulfilled, or Rejected, then save the latest data.");

        VBox workspaceCard = new VBox(8, heading, description);
        workspaceCard.setPadding(new Insets(20));
        workspaceCard.setStyle(UiStyle.card());

        VBox content = new VBox(7, welcome, instruction, cards, workspaceCard);
        content.setPadding(new Insets(32));
        return content;
    }

    private VBox createMetricCard(String title, String value, String accent, String caption) {
        Label titleLabel = new Label(title.toUpperCase());
        titleLabel.setStyle("-fx-text-fill: " + UiStyle.MUTED_TEXT + "; -fx-font-size: 10px;"
                + " -fx-font-weight: bold; -fx-letter-spacing: 0.6px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + accent + "; -fx-font-size: 27px; -fx-font-weight: bold;");

        Label captionLabel = new Label(caption);
        captionLabel.setStyle("-fx-text-fill: " + UiStyle.MUTED_TEXT + "; -fx-font-size: 12px;");

        VBox card = new VBox(7, titleLabel, valueLabel, captionLabel);
        card.setPadding(new Insets(18));
        card.setMinHeight(125);
        card.setStyle(UiStyle.card());
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private HBox createStatusBar() {
        Label status = new Label("●  " + navigator.getStatusMessage());
        status.setStyle("-fx-text-fill: " + UiStyle.MUTED_TEXT + "; -fx-font-size: 12px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label adminAccess = new Label("Admin access  ·  request review enabled");
        adminAccess.setStyle("-fx-text-fill: " + UiStyle.MUTED_TEXT + "; -fx-font-size: 12px;");

        HBox statusBar = new HBox(10, status, spacer, adminAccess);
        statusBar.setPadding(new Insets(11, 24, 11, 24));
        statusBar.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0;"
                + " -fx-border-width: 1 0 0 0;");
        return statusBar;
    }
}
