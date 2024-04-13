package com.spreadsheet.spreadsheetcelloperation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CircularDependencyException.class)
    public ResponseEntity<String> handleCircularDependencyException(CircularDependencyException circularDependencyException){

        return new ResponseEntity<>(circularDependencyException.getErrorMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException noSuchElementException){
        return new ResponseEntity<>(noSuchElementException.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCellIdException.class)
    public ResponseEntity<String> handleInvalidCellIdException(InvalidCellIdException invalidCellIdException){
        return new ResponseEntity<>(invalidCellIdException.getErrorMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SelfReferenceException.class)
    public ResponseEntity<String> handleSelfReferenceException(SelfReferenceException selfReferenceException){
        return new ResponseEntity<>(selfReferenceException.getErrorMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpressionEvaluationException.class)
    public ResponseEntity<String> handleExpressionEvaluationException(ExpressionEvaluationException evaluationException){
        return new ResponseEntity<>(evaluationException.getErrorMessage(),HttpStatus.UNPROCESSABLE_ENTITY);
    }


}
