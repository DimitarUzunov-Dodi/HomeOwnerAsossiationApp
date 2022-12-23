package nl.tudelft.sem.template.association.controllers;

import nl.tudelft.sem.template.association.models.ErrorModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    /**
     * Handles the IllegalArgumentException when thrown in a controller.
     *
     * @param e the exception
     * @return the response that should be given
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorModel> illegalArgument(IllegalArgumentException e) {
        ErrorModel message = new ErrorModel();
        message.setErrorMessage(e.getMessage());
        return ResponseEntity.badRequest().body(message);
    }

}
