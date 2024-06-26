package com.spreadsheet.spreadsheetcelloperation.service;

import com.spreadsheet.spreadsheetcelloperation.exception.ExpressionEvaluationException;
import com.spreadsheet.spreadsheetcelloperation.exception.InvalidCellIdException;
import com.spreadsheet.spreadsheetcelloperation.exception.SelfReferenceException;
import com.spreadsheet.spreadsheetcelloperation.model.Cell;
import com.spreadsheet.spreadsheetcelloperation.repository.CellRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CellOperationServiceTest {

    @InjectMocks
    private CellOperationService cellOperationService;

    @Mock
    private CellRepository cellRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Unit Test ***
    @Test
    void shouldThrowExceptionCalculateExpression(){

        String expression = "12+23)";
        assertThrows(ExpressionEvaluationException.class, () -> cellOperationService.calculateExpressionValue(expression));
    }

    @Test
    void shouldCalculateExpression(){

        String expression = "12+23";
        assertEquals("35.0",cellOperationService.calculateExpressionValue(expression));

        String expression1 = "12+23.03+(10-5)";
        assertEquals("40.03",cellOperationService.calculateExpressionValue(expression1));
    }

    @Test
    void shouldNotBeCircularDependent(){

        Cell A1 = new Cell("A1","=A4+A2+A3");
        Cell A2 = new Cell("A2","=12");
        Cell A3 = new Cell("A3","=13");
        Cell A4 = new Cell("A4","=14");

        List<Cell> dependencyList = new ArrayList<>();
        dependencyList.add(A2);
        dependencyList.add(A3);
        dependencyList.add(A4);

        A1.setDependentCells(dependencyList);

        assertEquals(false,cellOperationService.isCircularDependent(A1));
    }

    @Test
    void shouldBeCircularDependent(){

        Cell A1 = new Cell("A1","=A4+A2+A3");
        Cell A2 = new Cell("A2","=12");
        Cell A3 = new Cell("A3","=B2");
        Cell A4 = new Cell("A4","=14");

        List<Cell> dependencyList1 = new ArrayList<>();
        dependencyList1.add(A2);
        dependencyList1.add(A3);
        dependencyList1.add(A4);

        A1.setDependentCells(dependencyList1);

        Cell B2 = new Cell("B2","=A1");
        List<Cell> dependencyList2 = new ArrayList<>();
        dependencyList2.add(A1);
        B2.setDependentCells(dependencyList2);

        List<Cell> dependencyList3 = new ArrayList<>();
        dependencyList3.add(B2);
        A3.setDependentCells(dependencyList3);

        assertEquals(true,cellOperationService.isCircularDependent(A1));

    }

    @Test
    void shouldEvaluateExpressionToGenerateFinalExpression(){

        Cell A1 = new Cell("A1","=A4+A2+A3");
        Cell A2 = new Cell("A2","=12");
        Cell A3 = new Cell("A3","=13");
        Cell A4 = new Cell("A4","=14");

        List<Cell> dependencyList = new ArrayList<>();
        dependencyList.add(A2);
        dependencyList.add(A3);
        dependencyList.add(A4);

        A1.setDependentCells(dependencyList);

        Mockito.when(cellRepository.findById(A1.getCellId()))
                .thenReturn(Optional.of(A1));
        Mockito.when(cellRepository.findById(A2.getCellId()))
                .thenReturn(Optional.of(A2));
        Mockito.when(cellRepository.findById(A3.getCellId()))
                .thenReturn(Optional.of(A3));
        Mockito.when(cellRepository.findById(A4.getCellId()))
                .thenReturn(Optional.of(A4));

        String result = "=14+12+13";
        assertEquals(result,cellOperationService.evaluateExpression(A1.getData()));
    }

    @Test
    void shouldThrowNoSuchElementExceptionFromEvaluateExpression(){

        Cell A1 = new Cell("A1","=A5+A2+A3");
        assertThrows(NoSuchElementException.class,()->cellOperationService.evaluateExpression(A1.getData()));
    }

    @Test
    void shouldCreateDependencyListFromExpression(){

        Cell A1 = new Cell("A1","=A2+A3");
        Cell A2 = new Cell("A2","=12");
        Cell A3 = new Cell("A3","=13");

        List<Cell> dependencyList = new ArrayList<>();
        dependencyList.add(A2);
        dependencyList.add(A3);

        A1.setDependentCells(dependencyList);

        Mockito.when(cellRepository.findById(A1.getCellId()))
                .thenReturn(Optional.of(A1));
        Mockito.when(cellRepository.findById(A2.getCellId()))
                .thenReturn(Optional.of(A2));
        Mockito.when(cellRepository.findById(A3.getCellId()))
                .thenReturn(Optional.of(A3));

        assertEquals(dependencyList, cellOperationService.createDependencyListFromExpression(A1.getCellId(), A1.getData()));
    }

    @Test
    void shouldThrowInvalidClientIdExceptionFrom_CreateDependencyListFromExpression(){

        String cellId = "1";
        String data = "test";

        assertThrows(InvalidCellIdException.class, () -> cellOperationService.createDependencyListFromExpression(cellId,data));
    }

    // Integration Test ***
    @Test
    void shouldSaveDataInDatabase() {

        String cellId = "Z2";
        Object value = 123;
        Cell createdCell = new Cell(cellId, value.toString());

        Mockito.when(cellRepository.findById(cellId.toUpperCase())).thenReturn(Optional.of(createdCell));
        cellOperationService.setCellValue(cellId, value);
        verify(cellRepository, atLeastOnce()).save(createdCell);

        String cellId1 = "Z2";
        Object value1 = -123.89;
        Cell createdCell1 = new Cell(cellId1, value1.toString());

        Mockito.when(cellRepository.findById(cellId.toUpperCase())).thenReturn(Optional.of(createdCell1));
        cellOperationService.setCellValue(cellId, value);
        verify(cellRepository, atLeastOnce()).save(createdCell);
    }

    @Test
    void shouldThrowExceptionForInvalidClientId() {

        String cellId = "1";
        Object value = 123;

        assertThrows(InvalidCellIdException.class, () -> cellOperationService.setCellValue(cellId, value));
    }

    @Test
    void getValueForReferencedExpressionWithActualValue(){

        Cell A1 = new Cell("A1","=12+A2");
        Cell A2 = new Cell("A2","=12");

        List<Cell> dependencyList = new ArrayList<>();
        dependencyList.add(A2);

        A1.setDependentCells(dependencyList);

        Mockito.when(cellRepository.findById(A1.getCellId()))
                .thenReturn(Optional.of(A1));
        Mockito.when(cellRepository.findById(A2.getCellId()))
                .thenReturn(Optional.of(A2));

        assertEquals("24.0",cellOperationService.getCellValue(A1.getCellId()));
    }

    @Test
    void getValueForReferencedExpression(){

        Cell A1 = new Cell("A1","=A2");
        Cell A2 = new Cell("A2","=-12.0");

        List<Cell> dependencyList = new ArrayList<>();
        dependencyList.add(A2);

        A1.setDependentCells(dependencyList);

        Mockito.when(cellRepository.findById(A1.getCellId()))
                .thenReturn(Optional.of(A1));
        Mockito.when(cellRepository.findById(A2.getCellId()))
                .thenReturn(Optional.of(A2));

        assertEquals("-12.0",cellOperationService.getCellValue(A1.getCellId()));
    }

    @Test
    void getValueForCombinationOfParenthesisAndReference(){

        Cell A1 = new Cell("A1","=A4-(A2+A3)");
        Cell A2 = new Cell("A2","12");
        Cell A3 = new Cell("A3","13");
        Cell A4 = new Cell("A4","14");

        List<Cell> dependencyList = new ArrayList<>();
        dependencyList.add(A2);
        dependencyList.add(A3);
        dependencyList.add(A4);

        A1.setDependentCells(dependencyList);

        Mockito.when(cellRepository.findById(A1.getCellId()))
                .thenReturn(Optional.of(A1));
        Mockito.when(cellRepository.findById(A2.getCellId()))
                .thenReturn(Optional.of(A2));
        Mockito.when(cellRepository.findById(A3.getCellId()))
                .thenReturn(Optional.of(A3));
        Mockito.when(cellRepository.findById(A4.getCellId()))
                .thenReturn(Optional.of(A4));

        assertEquals("-11.0",cellOperationService.getCellValue(A1.getCellId()));
    }

    @Test
    void getValue_ThrowExpressionEvaluationExceptionForExpression(){

        Cell A1 = new Cell("A1","=A4-(A2+A3(");
        Cell A2 = new Cell("A2","12");
        Cell A3 = new Cell("A3","13");
        Cell A4 = new Cell("A4","14");

        List<Cell> dependencyList = new ArrayList<>();
        dependencyList.add(A2);
        dependencyList.add(A3);
        dependencyList.add(A4);

        A1.setDependentCells(dependencyList);

        Mockito.when(cellRepository.findById(A1.getCellId()))
                .thenReturn(Optional.of(A1));
        Mockito.when(cellRepository.findById(A2.getCellId()))
                .thenReturn(Optional.of(A2));
        Mockito.when(cellRepository.findById(A3.getCellId()))
                .thenReturn(Optional.of(A3));
        Mockito.when(cellRepository.findById(A4.getCellId()))
                .thenReturn(Optional.of(A4));

        assertThrows(ExpressionEvaluationException.class,() ->cellOperationService.getCellValue(A1.getCellId()));
    }

    @Test
    void getValue_ThrowInvalidCellIdException(){

        Cell A1 = new Cell("1","=A4-(A2+A3)");
        Cell A2 = new Cell("A2","12");
        Cell A3 = new Cell("A3","13");
        Cell A4 = new Cell("A4","14");

        List<Cell> dependencyList = new ArrayList<>();
        dependencyList.add(A2);
        dependencyList.add(A3);
        dependencyList.add(A4);

        A1.setDependentCells(dependencyList);

        assertThrows(InvalidCellIdException.class,() ->cellOperationService.getCellValue(A1.getCellId()));
    }

}