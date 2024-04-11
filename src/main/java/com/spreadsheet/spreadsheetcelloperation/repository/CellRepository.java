package com.spreadsheet.spreadsheetcelloperation.repository;

import com.spreadsheet.spreadsheetcelloperation.model.Cell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CellRepository extends JpaRepository<Cell,String> {
}
