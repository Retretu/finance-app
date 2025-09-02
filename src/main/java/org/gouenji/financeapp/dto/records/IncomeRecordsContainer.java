package org.gouenji.financeapp.dto.records;

import org.gouenji.financeapp.entity.records.IncomeRecord;

import java.util.List;

public class IncomeRecordsContainer {
    private final List<IncomeRecord> records;
    private final double total;
    private final Double filteredTotal;
    private final Double averageTotal;
    private final Double monthTotal;

    private IncomeRecordsContainer(Builder builder) {
        this.records = builder.records;
        this.total = builder.total;
        this.filteredTotal = builder.filteredTotal;
        this.averageTotal = builder.averageTotal;
        this.monthTotal = builder.monthTotal;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<IncomeRecord> records;
        private double total;
        private Double filteredTotal;
        private Double averageTotal;
        private Double monthTotal;

        public Builder records(List<IncomeRecord> records) {
            this.records = records;
            return this;
        }

        public Builder total(double total) {
            this.total = total;
            return this;
        }

        public Builder filteredTotal(Double filteredTotal) {
            this.filteredTotal = filteredTotal;
            return this;
        }

        public Builder averageTotal(Double averageTotal) {
            this.averageTotal = averageTotal;
            return this;
        }

        public Builder monthTotal(Double monthTotal) {
            this.monthTotal = monthTotal;
            return this;
        }

        public IncomeRecordsContainer build() {
            return new IncomeRecordsContainer(this);
        }
    }

    public List<IncomeRecord> getRecords() {
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