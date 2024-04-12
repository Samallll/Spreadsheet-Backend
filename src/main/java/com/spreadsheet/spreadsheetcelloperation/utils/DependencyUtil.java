package com.spreadsheet.spreadsheetcelloperation.utils;

import com.spreadsheet.spreadsheetcelloperation.model.Cell;

import java.util.Set;

public class DependencyUtil {

    public static boolean hasCircularDependency(Cell cell, Set<Cell> visited, Set<Cell> inPath) {
        if (inPath.contains(cell)) {
            return true;
        }

        if (visited.contains(cell)) {
            return false;
        }

        visited.add(cell);
        inPath.add(cell);

        for (Cell dependentCell : cell.getDependentCells()) {
            if (hasCircularDependency(dependentCell, visited, inPath)) {
                return true;
            }
        }
        inPath.remove(cell);
        return false;
    }

}
