package nl.tudelft.sem.template.authentication.domain.user;

/**
 * Exception to indicate the MemberID is already in use.
 */
public class MemberIdAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;
    
    public MemberIdAlreadyInUseException(MemberId memberId) {
        super(memberId.toString());
    }
}
