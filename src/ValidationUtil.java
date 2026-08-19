public final class ValidationUtil {
    private ValidationUtil() {
        // Utility class: no object should be created.
    }

    public static String requireText(String value, String fieldName)
            throws InvalidDataFormatException {
        String cleanedValue = value == null ? "" : value.trim();
        if (cleanedValue.isEmpty()) {
            throw new InvalidDataFormatException(fieldName + " is required.");
        }
        if (cleanedValue.contains("|")) {
            throw new InvalidDataFormatException(fieldName + " cannot contain the | symbol.");
        }
        return cleanedValue;
    }

    public static String validatePhone(String phone) throws InvalidDataFormatException {
        String cleanedPhone = requireText(phone, "Phone number").replaceAll("[ -]", "");
        if (!cleanedPhone.matches("\\d{8,15}")) {
            throw new InvalidDataFormatException(
                    "Phone number must contain 8 to 15 digits.");
        }
        return cleanedPhone;
    }

    public static int parsePositiveInteger(String value, String fieldName)
            throws InvalidDataFormatException {
        try {
            int number = Integer.parseInt(requireText(value, fieldName));
            if (number <= 0) {
                throw new InvalidDataFormatException(fieldName + " must be greater than zero.");
            }
            return number;
        } catch (NumberFormatException ex) {
            throw new InvalidDataFormatException(fieldName + " must be a whole number.");
        }
    }
}
