package com.spreadsheet.spreadsheetcelloperation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Cell {

    @Id
    String cellId;
    @Column
    String data;
    @ManyToMany
    List<Cell> dependentCells;

    public Cell(){

    }

    public Cell(String cellId, String data) {
        this.cellId = cellId;
        this.data = data;
        this.dependentCells = new ArrayList<>();
    }

    public Cell(String cellId){
        this.cellId = cellId;
        this.dependentCells = new ArrayList<>();
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<Cell> getDependentCells() {
        return dependentCells;
    }

    public void setDependentCells(List<Cell> dependentCells) {
        this.dependentCells = dependentCells;
    }
}
