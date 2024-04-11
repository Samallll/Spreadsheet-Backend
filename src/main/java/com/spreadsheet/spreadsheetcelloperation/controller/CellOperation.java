package com.spreadsheet.spreadsheetcelloperation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cells")
@Tag(name = "Cell Operations")
public class CellOperation {

    @PostMapping("/{cellId}/value")
    public ResponseEntity<?> addValueToCell(@PathVariable("cellId") String cellId,
                                            @RequestBody Object value){

        return ResponseEntity.ok("Successfull");
    }
}
