package com.spreadsheet.spreadsheetcelloperation.exception;

public class InvalidCellIdException extends RuntimeException{

    private String errorMessage;

    public InvalidCellIdException(String errorMessage){
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
