package com.spreadsheet.spreadsheetcelloperation.service;

import com.spreadsheet.spreadsheetcelloperation.exception.ExpressionEvaluationException;
import com.spreadsheet.spreadsheetcelloperation.exception.InvalidCellIdException;
import com.spreadsheet.spreadsheetcelloperation.model.Cell;
import com.spreadsheet.spreadsheetcelloperation.repository.CellRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

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
}