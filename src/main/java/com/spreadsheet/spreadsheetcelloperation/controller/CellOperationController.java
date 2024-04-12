package com.spreadsheet.spreadsheetcelloperation.controller;

import com.spreadsheet.spreadsheetcelloperation.service.CellOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cells")
@Tag(name = "Cell Operations")
public class CellOperationController {

    private final CellOperation cellOperationService;

    public CellOperationController(CellOperation cellOperation){
        this.cellOperationService = cellOperation;
    }

    @PostMapping("/{cellId}/value")
    public ResponseEntity<?> addValueToCell(@PathVariable("cellId") String cellId,
                                            @RequestBody Object value){

        cellOperationService.setCellValue(cellId,value);
        return ResponseEntity.ok("Successfull");
    }

    @GetMapping("/{cellId}")
    public ResponseEntity<String> getValueFromCell(@PathVariable("cellId") String cellId){

        return ResponseEntity.ok(cellOperationService.getCellValue(cellId));
    }
}
