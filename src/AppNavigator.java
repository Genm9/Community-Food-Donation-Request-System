public interface AppNavigator {
    void showLogin();

    void showRegister();

    void showDashboard();

    void completeLogin(String username, String role);

    void showDonationForm();

    void showRequestForm();

    void showAdminView();

    void showSummaryView();

    void saveData();

    void loadData();

    void exitApplication();

    String getLoggedInUsername();

    String getLoggedInRole();

    void logout();

    String getStatusMessage();
}
