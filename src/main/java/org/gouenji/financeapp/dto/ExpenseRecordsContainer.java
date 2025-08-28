package org.gouenji.financeapp.dto;

import org.gouenji.financeapp.entity.ExpenseRecord;

import java.util.List;

public class ExpenseRecordsContainer {
    private final List<ExpenseRecord> records;
    private final double total;
    private final Double filteredTotal;
    private final Double averageTotal;
    private final Double monthTotal;

    private ExpenseRecordsContainer(Builder builder) {
        this.records = builder.records;
        this.total = builder.total;
        this.filteredTotal = builder.filteredTotal;
        this.averageTotal = builder.averageTotal;
        this.monthTotal = builder.monthTotal;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private List<ExpenseRecord> records;
        private double total;
        private Double filteredTotal;
        private Double averageTotal;
        private Double monthTotal;

        public Builder records(List<ExpenseRecord> records){
            this.records = records;
            return this;
        }

        public Builder total(double total){
            this.total = total;
            return this;
        }

        public Builder filteredTotal(Double filteredTotal){
            this.filteredTotal = filteredTotal;
            return this;
        }

        public Builder averageTotal(Double averageTotal){
            this.averageTotal = averageTotal;
            return this;
        }

        public Builder monthTotal(Double monthTotal){
            this.monthTotal = monthTotal;
            return this;
        }

        public ExpenseRecordsContainer build(){
            return new ExpenseRecordsContainer(this);
        }
    }

    public List<ExpenseRecord> getRecords() {
        return records;
    }

    public double getTotal() {
        return total;
    }

    public Double getFilteredTotal() {
        return filteredTotal;
    }

    public boolean hasFilteredTotal() {
        return filteredTotal != null;
    }

    public Double getAverageTotal() {
        return averageTotal;
    }

    public boolean hasAverageTotal() {
        return averageTotal != null;
    }

    public Double getMonthTotal() {
        return monthTotal;
    }

    public boolean hasMonthTotal() {
        return monthTotal != null;
    }
}
