package com.spreadsheet.spreadsheetcelloperation.service;

import org.springframework.stereotype.Service;

@Service
public class CellOperationService implements CellOperation {

    @Override
    public void setCellValue(String cellId, Object value) {

    }

    @Override
    public int getCellValue(String cellId) {
        return 0;
    }
}
