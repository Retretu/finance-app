package org.gouenji.financeapp.entity.records;

import jakarta.persistence.*;
import org.gouenji.financeapp.entity.User;
import org.gouenji.financeapp.entity.enums.records.ExpenseCategory;

import java.time.LocalDate;

@Entity
@Table(name = "expense_records")
public class ExpenseRecord implements Record{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 35)
    private ExpenseCategory category;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "description", length = 100)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public ExpenseRecord() {
    }

    public ExpenseRecord(ExpenseCategory category,
                         double amount,
                         LocalDate date,
                         String description,
                         User user) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.user = user;
    }

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

    @Override
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

    @Override
    public String getType() {
        return "EXPENSE";
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
