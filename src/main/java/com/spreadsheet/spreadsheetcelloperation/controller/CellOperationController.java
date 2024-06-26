package com.spreadsheet.spreadsheetcelloperation.controller;

import com.spreadsheet.spreadsheetcelloperation.service.CellOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
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

    @Operation(
            description = "Endpoint for store cell",
            summary = "This endpoint will update the cell if it exists. Otherwise it will create a new cell.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "202"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    )
            }
    )
    @PostMapping("/{cellId}/value")
    public ResponseEntity<String> addValueToCell(@PathVariable("cellId") String cellId,
                                            @RequestBody Object value){

        cellOperationService.setCellValue(cellId,value);
        return new ResponseEntity<>("Success", HttpStatus.ACCEPTED);
    }

    @Operation(
            description = "Endpoint for get cell value",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    )
            }
    )
    @GetMapping("/{cellId}")
    public ResponseEntity<String> getValueFromCell(@PathVariable("cellId") String cellId){

        return ResponseEntity.ok(cellOperationService.getCellValue(cellId));
    }
}
