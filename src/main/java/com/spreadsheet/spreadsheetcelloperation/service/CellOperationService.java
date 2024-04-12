package com.spreadsheet.spreadsheetcelloperation.service;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.spreadsheet.spreadsheetcelloperation.model.Cell;
import com.spreadsheet.spreadsheetcelloperation.repository.CellRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CellOperationService implements CellOperation {

    private final CellRepository cellRepository;

    private static final Logger logger = Logger.getLogger(CellOperationService.class.getName());

    private static final String CELL_ID_PATTERN = "^[A-Z]\\d+$";

    public CellOperationService(CellRepository cellRepository){
        this.cellRepository = cellRepository;
    }

    /**
     * Sets the value of a cell with the specified ID.
     * Create new cells for the given cell as the dependentCells.
     * Update the data in the given cell if the cell exists.
     *
     * @param cellId The ID of the cell.
     * @param value  The new value of the cell.
     * @throws NoSuchElementException if the cell ID is invalid.
     */
    @Override
    @Transactional
    public void setCellValue(String cellId, Object value) {

        String data = value.toString();
        List<Cell> dependencyList = new ArrayList<>();
        if(!cellId.matches(CELL_ID_PATTERN)) {
            throw new NoSuchElementException("Invalid Cell Id");
        }
        if(data.startsWith("=")){
            dependencyList = createDependencyListFromExpression(cellId, data);
        }

        Cell cell = cellRepository.findById(cellId)
                .orElseGet(() -> {
                    Cell newCell = new Cell(cellId.toUpperCase(), value.toString());
                    cellRepository.save(newCell);
                    logger.info("New cell saved to database, Cell Id: "+ cellId);
                    return newCell;
                });

        cell.setData(data);
        cell.setDependentCells(dependencyList);
        cellRepository.save(cell);
        logger.info("Existing cell updated, Cell Id: "+ cell.getCellId());
    }

    /**
     * Creates a list dependent cells for a given cell based on the expression data.
     *
     * @param cellId The ID of the cell for which dependencies are being created.
     * @param data   The expression data containing cell references.
     * @return A list of cells that the given cell depends on.
     * @throws IllegalStateException for cells referring to the same cell.
     * @throws NoSuchElementException for invalid cellId
     */
    private List<Cell> createDependencyListFromExpression(String cellId, String data) {

        if(!cellId.matches(CELL_ID_PATTERN)) {
            throw new NoSuchElementException("Invalid Cell Id");
        }
        List<Cell> dependencyList = new ArrayList<>();
        String extractDependency = data.substring(1);
        String[] tokens = extractDependency.split("(?=[+\\-*/()])|(?<=[+\\-*/()])");
        Pattern cellIdPattern = Pattern.compile(CELL_ID_PATTERN);
        for (String token : tokens) {
            Matcher cellIdMatcher = cellIdPattern.matcher(token);
            if (cellIdMatcher.matches()) {
                if(cellId.equals(token.toUpperCase())){
                    throw new IllegalStateException("Reference Error - Referring to Same element");
                }
                Cell cell = cellRepository.findById(token.toUpperCase()).orElseGet(
                        () -> {
                            Cell newCell = new Cell(token);
                            return cellRepository.save(newCell);
                        }
                );
                dependencyList.add(cell);
            }
        }
        return dependencyList;
    }

    private String evaluateEquation(String data) {
        // TODO -- evaluate the equation and return the actual final data
        String expression = data.substring(1);

        DoubleEvaluator evaluator = new DoubleEvaluator();
        Double result = evaluator.evaluate(expression);
        System.out.println(result);

        return expression;
    }


    @Override
    public int getCellValue(String cellId) {
        return 0;
    }
}
