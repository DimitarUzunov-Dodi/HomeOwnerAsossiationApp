package nl.tudelft.sem.template.voting.domain.rulevoting;

public class RuleTooLongException extends Exception {
    static final long serialVersionUID = -1232344354532L;

    public RuleTooLongException(String message) {
        super(message);
    }
}
