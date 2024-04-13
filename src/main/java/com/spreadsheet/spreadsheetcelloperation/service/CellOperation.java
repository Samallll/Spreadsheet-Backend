package com.spreadsheet.spreadsheetcelloperation.service;

import com.spreadsheet.spreadsheetcelloperation.model.Cell;

public interface CellOperation {

    void setCellValue(String cellId, Object value);
    String getCellValue(String cellId);

}
