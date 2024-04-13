package com.spreadsheet.spreadsheetcelloperation.service;

import com.spreadsheet.spreadsheetcelloperation.exception.ExpressionEvaluationException;
import com.spreadsheet.spreadsheetcelloperation.exception.InvalidCellIdException;
import com.spreadsheet.spreadsheetcelloperation.model.Cell;
import com.spreadsheet.spreadsheetcelloperation.repository.CellRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;
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
        Cell A2 = new Cell("A1","=12");
        Cell A3 = new Cell("A1","=13");
        Cell A4 = new Cell("A1","=14");

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
}