package org.gouenji.financeapp.dto;

import org.gouenji.financeapp.entity.ExpenseRecord;

import java.util.List;

public class ExpenseRecordsContainer {
    private final List<ExpenseRecord> records;
    private final double total;

    public ExpenseRecordsContainer(List<ExpenseRecord> records, double total) {
        this.records = records;
        this.total = total;
    }

    public List<ExpenseRecord> getRecords() {
        return records;
    }

    public double getTotal() {
        return total;
    }
}
