public class UserAccount {
    private final String username;
    private final String password;
    private final String role;

    public UserAccount(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role == null || role.isBlank() ? "User" : role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String toFileString() {
        return FileStorageService.sanitize(username) + "|"
                + FileStorageService.sanitize(password) + "|"
                + FileStorageService.sanitize(role);
    }
}
