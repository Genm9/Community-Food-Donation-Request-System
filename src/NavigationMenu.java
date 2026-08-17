import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Shared taskbar for User and Admin screens.
 * The active page is shown with a coloured button and a left accent line.
 */
public final class NavigationMenu {
    private NavigationMenu() {
    }

    public static VBox userMenu(AppNavigator navigator, String activePage) {
        VBox menu = createMenu("USER MENU", UiStyle.USER_ACCENT);
        menu.getChildren().addAll(
                navButton("Dashboard", "dashboard", activePage, UiStyle.USER_ACCENT,
                        navigator::showDashboard),
                navButton("Donate Food", "donate", activePage, UiStyle.USER_ACCENT,
                        navigator::showDonationForm),
                navButton("Request Food", "request", activePage, UiStyle.USER_ACCENT,
                        navigator::showRequestForm),
                navButton("Summary", "summary", activePage, UiStyle.USER_ACCENT,
                        navigator::showSummaryView),
                new Separator(),
                navButton("Save Data", "save", activePage, UiStyle.USER_ACCENT,
                        navigator::saveData),
                navButton("Load Data", "load", activePage, UiStyle.USER_ACCENT,
                        navigator::loadData)
        );
        addAccountActions(menu, navigator, UiStyle.USER_ACCENT);
        return menu;
    }

    public static VBox adminMenu(AppNavigator navigator, String activePage) {
        VBox menu = createMenu("ADMIN MENU", UiStyle.ADMIN_ACCENT);
        menu.getChildren().addAll(
                navButton("Admin Dashboard", "admin-dashboard", activePage, UiStyle.ADMIN_ACCENT,
                        navigator::showDashboard),
                navButton("Manage Records", "records", activePage, UiStyle.ADMIN_ACCENT,
                        navigator::showAdminView),
                navButton("Summary", "summary", activePage, UiStyle.ADMIN_ACCENT,
                        navigator::showSummaryView),
                new Separator(),
                navButton("Save Data", "save", activePage, UiStyle.ADMIN_ACCENT,
                        navigator::saveData),
                navButton("Load Data", "load", activePage, UiStyle.ADMIN_ACCENT,
                        navigator::loadData)
        );
        addAccountActions(menu, navigator, UiStyle.ADMIN_ACCENT);
        return menu;
    }

    private static VBox createMenu(String title, String accent) {
        Label menuTitle = new Label(title);
        menuTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: "
                + accent + "; -fx-letter-spacing: 1.2px;");

        VBox menu = new VBox(7, menuTitle);
        menu.setPadding(new Insets(22, 14, 18, 14));
        menu.setPrefWidth(220);
        menu.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0;"
                + " -fx-border-width: 0 1 0 0;");
        return menu;
    }

    private static void addAccountActions(VBox menu, AppNavigator navigator, String accent) {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        menu.getChildren().add(spacer);

        Button logout = new Button("Logout");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setPrefHeight(40);
        logout.setAlignment(Pos.CENTER_LEFT);
        logout.setStyle("-fx-background-color: #FFF7ED; -fx-text-fill: #C2410C;"
                + " -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 0 14;"
                + " -fx-cursor: hand;");
        logout.setOnAction(event -> navigator.logout());
        logout.setOnMouseEntered(event -> logout.setStyle(
                "-fx-background-color: #FFEDD5; -fx-text-fill: #9A3412;"
                        + " -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 0 14;"
                        + " -fx-cursor: hand;"));
        logout.setOnMouseExited(event -> logout.setStyle(
                "-fx-background-color: #FFF7ED; -fx-text-fill: #C2410C;"
                        + " -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 0 14;"
                        + " -fx-cursor: hand;"));

        Button exit = new Button("Exit Application");
        exit.setMaxWidth(Double.MAX_VALUE);
        exit.setPrefHeight(40);
        exit.setAlignment(Pos.CENTER_LEFT);
        exit.setStyle("-fx-background-color: #FEF2F2; -fx-text-fill: #B91C1C;"
                + " -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 0 14;"
                + " -fx-cursor: hand;");
        exit.setOnAction(event -> navigator.exitApplication());

        menu.getChildren().addAll(logout, exit);
    }

    private static Button navButton(String text, String page, String activePage,
                                    String accent, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(42);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(0, 12, 0, 16));
        boolean active = page.equals(activePage);
        applyButtonStyle(button, active, accent);
        button.setOnAction(event -> action.run());
        button.setOnMouseEntered(event -> {
            if (!page.equals(activePage)) {
                button.setStyle(hoverStyle());
            }
        });
        button.setOnMouseExited(event -> applyButtonStyle(button, page.equals(activePage), accent));
        return button;
    }

    private static void applyButtonStyle(Button button, boolean active, String accent) {
        if (active) {
            button.setStyle("-fx-background-color: " + accent + "; -fx-text-fill: white;"
                    + " -fx-font-weight: bold; -fx-background-radius: 8;"
                    + " -fx-padding: 0 12 0 16; -fx-border-color: " + accent + ";"
                    + " -fx-border-width: 0 0 0 4; -fx-cursor: hand;");
        } else {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: #475569;"
                    + " -fx-font-weight: bold; -fx-background-radius: 8;"
                    + " -fx-padding: 0 12 0 16; -fx-cursor: hand;");
        }
    }

    private static String hoverStyle() {
        return "-fx-background-color: #E2E8F0; -fx-text-fill: #0F172A;"
                + " -fx-font-weight: bold; -fx-background-radius: 8;"
                + " -fx-padding: 0 12 0 16; -fx-cursor: hand;";
    }
}
