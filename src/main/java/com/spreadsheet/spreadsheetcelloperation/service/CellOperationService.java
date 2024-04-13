package com.spreadsheet.spreadsheetcelloperation.service;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.spreadsheet.spreadsheetcelloperation.exception.CircularDependencyException;
import com.spreadsheet.spreadsheetcelloperation.exception.ExpressionEvaluationException;
import com.spreadsheet.spreadsheetcelloperation.exception.InvalidCellIdException;
import com.spreadsheet.spreadsheetcelloperation.exception.SelfReferenceException;
import com.spreadsheet.spreadsheetcelloperation.model.Cell;
import com.spreadsheet.spreadsheetcelloperation.repository.CellRepository;
import com.spreadsheet.spreadsheetcelloperation.utils.DependencyUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
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
     * Sets the value of a cell with the specified ID,
     * Create new cells for the given cell as the dependentCells and
     * Update the data in the given cell if the cell exists.
     *
     * @param cellId The ID of the cell.
     * @param value  The new value of the cell.
     * @throws InvalidCellIdException if the cell ID is invalid.
     */
    @Override
    @Transactional
    public void setCellValue(String cellId, Object value) {

        String data = value.toString();
        List<Cell> dependencyList = new ArrayList<>();
        if(!cellId.matches(CELL_ID_PATTERN)) {
            throw new InvalidCellIdException("Invalid Cell Id provided");
        }
        if(data.startsWith("=")){
            dependencyList = createDependencyListFromExpression(cellId, data.substring(1));
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
    }

    /**
     * Creates a list of dependent cells for a given cell based on the expression.
     *
     * @param cellId The ID of the cell for which dependencies are being created.
     * @param data   The expression data containing cell references.
     * @return A list of cells that the given cell depends on.
     * @throws SelfReferenceException for cells referring to the same cell.
     * @throws InvalidCellIdException for invalid cellId
     */
    List<Cell> createDependencyListFromExpression(String cellId, String data) {

        if(!cellId.matches(CELL_ID_PATTERN)) {
            throw new InvalidCellIdException("Invalid Cell Id provided");
        }
        List<Cell> dependencyList = new ArrayList<>();
        String[] tokens = data.split("(?=[+\\-*/()])|(?<=[+\\-*/()])");
        Pattern cellIdPattern = Pattern.compile(CELL_ID_PATTERN);
        for (String token : tokens) {
            Matcher cellIdMatcher = cellIdPattern.matcher(token);
            if (cellIdMatcher.matches()) {
                if(cellId.equals(token.toUpperCase())){
                    throw new SelfReferenceException("Reference Error : Referring to Same Cell Id");
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

    @Override
    public String getCellValue(String cellId) {

        if(!cellId.matches(CELL_ID_PATTERN)) {
            throw new InvalidCellIdException("Invalid Cell Id provided");
        }
        Cell cell = cellRepository.findById(cellId)
                .orElseThrow(() -> new NoSuchElementException("Invalid Cell Id : Cell doesn't exist"));
        if(isCircularDependent(cell)){
            throw new CircularDependencyException("Reference Error : Circular Dependency Found");
        }
        String result;
        if(cell.getData().startsWith("=")){
            result = evaluateExpression(cell.getData().substring(1));
            result = calculateExpressionValue(result);
        }
        else{
            result = cell.getData();
        }
        return result;
    }

    /**
     * Converts an expression with references to an executable expression with original values.
     * @param data The expression to be changed into the final executable expression
     * @throws NoSuchElementException cell Id represents which doesn't exist
     * @return final expression which can be evaluated by an expression evaluator.
     */
    String evaluateExpression(String data) {
        String[] tokens = data.split("(?=[+\\-*/()])|(?<=[+\\-*/()])");
        Pattern cellIdPattern = Pattern.compile(CELL_ID_PATTERN);
        StringBuilder finalExpression = new StringBuilder();
        // Introduce a Map to cache evaluated cell values
        Map<String, String> evaluatedValues = new HashMap<>();

        for (String token : tokens) {
            Matcher cellIdMatcher = cellIdPattern.matcher(token);
            if (cellIdMatcher.matches()) {
                String cellId = token;
                // Check if the value for this cell ID is already cached
                String evaluatedValue = evaluatedValues.get(cellId);
                if (evaluatedValue == null) {
                    Cell cell = cellRepository.findById(cellId).orElseThrow(() -> new NoSuchElementException("Invalid Cell Id : Cell doesn't exist"));
                    evaluatedValue = evaluateExpression(cell.getData().replaceFirst("^=", ""));
                    // Cache the evaluated value for future reference
                    evaluatedValues.put(cellId, evaluatedValue);
                }
                finalExpression.append(evaluatedValue);
            } else {
                finalExpression.append(token);
            }
        }
        logger.info("Final Expression Generated: "+ finalExpression.toString());
        return finalExpression.toString();
    }


    String calculateExpressionValue(String expression) {

        try{
            DoubleEvaluator evaluator = new DoubleEvaluator();
            Double answer = evaluator.evaluate(expression);
            return answer.toString();
        }
        catch(RuntimeException e){
            throw new ExpressionEvaluationException("Error evaluating expression: "+ e.getMessage());
        }
    }

    Boolean isCircularDependent(Cell cell) {

        if(cell == null){
            return null;
        }
        List<Cell> dependentList = cell.getDependentCells();
        Set<Cell> visited = new HashSet<>();
        Set<Cell> inPath = new HashSet<>();

        for (Cell cell1 : dependentList) {
            if (DependencyUtil.hasCircularDependency(cell1, visited, inPath)) {
                logger.info("Circular Dependency Found");
                return true;
            }
        }
        return false;
    }

}
