
/**
 * Thrown when a saved text-file record does not follow the expected format.
 */
public class InvalidDataFormatException extends Exception {
    public InvalidDataFormatException(String message) {
        super(message);
    }
}
