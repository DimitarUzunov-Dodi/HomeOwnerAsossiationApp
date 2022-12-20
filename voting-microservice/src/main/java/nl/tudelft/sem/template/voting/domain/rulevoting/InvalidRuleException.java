package nl.tudelft.sem.template.voting.domain.rulevoting;

public class InvalidRuleException extends Exception {
    static final long serialVersionUID = -12323443432532L;

    public InvalidRuleException(String message) {
        super(message);
    }
}
