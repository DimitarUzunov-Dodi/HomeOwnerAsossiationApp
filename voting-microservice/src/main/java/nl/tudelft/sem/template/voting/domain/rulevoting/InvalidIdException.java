package nl.tudelft.sem.template.voting.domain.rulevoting;

public class InvalidIdException extends Exception {
    static final long serialVersionUID = -1232332343532L;

    public InvalidIdException(String message) {
        super(message);
    }
}
