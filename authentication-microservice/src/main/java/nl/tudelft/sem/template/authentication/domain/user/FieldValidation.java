package nl.tudelft.sem.template.authentication.domain.user;

public class FieldValidation {
    /**
     * Validates a field (userId or password). Checks if the string in
     * question is null, longer than 24 characters or containing
     * forbidden characters.
     *
     * @return whether the field is valid
     */
    public static boolean validateField(String str) {
        if (str == null || str.length() <= 2  || str.length() > 24 || !str.matches("^[a-zA-Z0-9_]+$")) {
            return false;
        } else {
            return true;
        }
    }
}
