import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;

/**
 * Shared visual styles for the JavaFX application.
 * Keeping styles in one class makes the interface consistent and easier to maintain.
 */
public final class UiStyle {
    public static final String USER_ACCENT = "#2563EB";
    public static final String ADMIN_ACCENT = "#475569";
    public static final String SUCCESS = "#16A34A";
    public static final String WARNING = "#D97706";
    public static final String DANGER = "#DC2626";
    public static final String TEXT = "#0F172A";
    public static final String MUTED_TEXT = "#64748B";
    public static final String PAGE_BACKGROUND = "#F1F5F9";
    public static final String CARD_BACKGROUND = "#FFFFFF";
    public static final String BORDER = "#E2E8F0";

    private UiStyle() {
    }

    public static String pageBackground() {
        return "-fx-background-color: " + PAGE_BACKGROUND + ";";
    }

    public static String header(String startColour, String endColour) {
        return "-fx-background-color: linear-gradient(to right, " + startColour + ", "
                + endColour + ");";
    }

    public static String card() {
        return "-fx-background-color: " + CARD_BACKGROUND + ";"
                + " -fx-background-radius: 14;"
                + " -fx-border-color: " + BORDER + ";"
                + " -fx-border-radius: 14;"
                + " -fx-effect: dropshadow(gaussian, rgba(15,23,42,0.10), 18, 0.18, 0, 5);";
    }

    public static String input() {
        return "-fx-background-color: #FFFFFF;"
                + " -fx-border-color: #CBD5E1;"
                + " -fx-border-radius: 8;"
                + " -fx-background-radius: 8;"
                + " -fx-padding: 10 12;"
                + " -fx-font-size: 13px;";
    }

    public static void styleInput(Control control) {
        control.setStyle(input());
    }

    public static Button primaryButton(String text, String accent, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(40);
        button.setStyle(primaryButtonStyle(accent));
        button.setOnAction(event -> action.run());
        addHover(button, primaryButtonStyle(accent),
                "-fx-background-color: derive(" + accent + ", -12%); -fx-text-fill: white;"
                        + " -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        return button;
    }

    public static Button secondaryButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefHeight(38);
        button.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: " + TEXT + ";"
                + " -fx-border-color: " + BORDER + "; -fx-border-radius: 8;"
                + " -fx-background-radius: 8; -fx-padding: 9 16;");
        button.setOnAction(event -> action.run());
        addHover(button, button.getStyle(),
                "-fx-background-color: #E2E8F0; -fx-text-fill: " + TEXT + ";"
                        + " -fx-border-color: #CBD5E1; -fx-border-radius: 8;"
                        + " -fx-background-radius: 8; -fx-padding: 9 16; -fx-cursor: hand;");
        return button;
    }

    public static Button dangerButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefHeight(38);
        button.setStyle("-fx-background-color: #FEF2F2; -fx-text-fill: " + DANGER + ";"
                + " -fx-border-color: #FECACA; -fx-border-radius: 8;"
                + " -fx-background-radius: 8; -fx-padding: 9 16;");
        button.setOnAction(event -> action.run());
        addHover(button, button.getStyle(),
                "-fx-background-color: #FEE2E2; -fx-text-fill: #B91C1C;"
                        + " -fx-border-color: #FCA5A5; -fx-border-radius: 8;"
                        + " -fx-background-radius: 8; -fx-padding: 9 16; -fx-cursor: hand;");
        return button;
    }

    public static Label pageTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: " + TEXT + ";");
        return label;
    }

    public static Label pageSubtitle(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: " + MUTED_TEXT + ";");
        return label;
    }

    public static Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT + ";");
        return label;
    }

    private static String primaryButtonStyle(String accent) {
        return "-fx-background-color: " + accent + "; -fx-text-fill: white;"
                + " -fx-font-weight: bold; -fx-background-radius: 8;"
                + " -fx-padding: 10 18; -fx-cursor: hand;";
    }

    private static void addHover(Button button, String normal, String hover) {
        button.setOnMouseEntered(event -> button.setStyle(hover));
        button.setOnMouseExited(event -> button.setStyle(normal));
    }
}
