package org.gouenji.financeapp.entity;

import jakarta.persistence.*;
import org.gouenji.financeapp.entity.enumsRecords.ExpenseCategory;

import java.time.LocalDate;

@Entity
@Table(name = "expense_records")
public class ExpenseRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ExpenseCategory category;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "description")
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
