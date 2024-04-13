package com.spreadsheet.spreadsheetcelloperation.exception;

public class ExpressionEvaluationException extends RuntimeException{

    private String errorMessage;

    public ExpressionEvaluationException(String errorMessage){
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
