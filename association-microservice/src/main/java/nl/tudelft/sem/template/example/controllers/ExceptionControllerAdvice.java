package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.models.ErrorModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorModel> illegalArgument(IllegalArgumentException e){
        ErrorModel message = new ErrorModel();
        message.setErrorMessage(e.getMessage());
        return ResponseEntity.badRequest().body(message);
    }

}
