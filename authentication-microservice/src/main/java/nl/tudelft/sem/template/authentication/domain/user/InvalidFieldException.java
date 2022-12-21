package nl.tudelft.sem.template.authentication.domain.user;

/**
 * Exception to indicate the field is invalid.
 */
public class InvalidFieldException extends Exception {
    static final long serialVersionUID = -3387516993334229948L;

    public InvalidFieldException() {
        super("One or more fields are invalid!");
    }
}
