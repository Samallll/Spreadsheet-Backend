package com.spreadsheet.spreadsheetcelloperation.service;

public interface CellOperation {

    void setCellValue(String cellId, Object value);
    String getCellValue(String cellId);

}
