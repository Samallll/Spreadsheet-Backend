package com.spreadsheet.spreadsheetcelloperation.service;

import com.spreadsheet.spreadsheetcelloperation.model.Cell;
import com.spreadsheet.spreadsheetcelloperation.repository.CellRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class CellOperationService implements CellOperation {

    private final CellRepository cellRepository;

    private static final Logger logger = Logger.getLogger(CellOperationService.class.getName());

    private static final String CELL_ID_PATTERN = "^[A-Z]\\d+$";

    public CellOperationService(CellRepository cellRepository){
        this.cellRepository = cellRepository;
    }

    @Override
    public void setCellValue(String cellId, Object value) {

        String data = value.toString();
        if(!cellId.matches(CELL_ID_PATTERN)){
            throw new NoSuchElementException("Invalid Cell Id");
        }
        if(data.startsWith("=")) {
            data = evaluateEquation(value.toString());
        }
        Cell cell = cellRepository.findById(cellId)
                .orElseGet(() -> {
                    Cell newCell = new Cell(cellId.toUpperCase(), value.toString());
                    cellRepository.save(newCell);
                    logger.info("New cell saved to database: Cell Id: "+ cellId);
                    return newCell;
                });

        cell.setData(data);
        cellRepository.save(cell);
        logger.info("Existing cell updated, Cell Id: "+ cell.getCellId());
    }

    private String evaluateEquation(String data) {
        // TODO -- evaluate the equation and return the actual final data
        String finalData;
        if(isFunction(data.substring(1,data.length()))){
            finalData = "";
        }
        else {
            finalData = "";
        }
        return finalData;
    }

    private boolean isFunction(String function) {
        // TODO -- validate whether the given string is a function
        function.toUpperCase();
        return true;
    }

    @Override
    public int getCellValue(String cellId) {
        return 0;
    }
}
