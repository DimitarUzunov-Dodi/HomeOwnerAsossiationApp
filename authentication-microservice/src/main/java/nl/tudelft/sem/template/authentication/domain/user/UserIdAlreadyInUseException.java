package nl.tudelft.sem.template.authentication.domain.user;

/**
 * Exception to indicate the UserID is already in use.
 */
public class UserIdAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;
    
    public UserIdAlreadyInUseException(UserId userId) {
        super(userId.toString());
    }
}
