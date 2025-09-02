package org.gouenji.financeapp.entity.records;

import jakarta.persistence.*;
import org.gouenji.financeapp.entity.User;
import org.gouenji.financeapp.entity.enums.records.IncomeCategory;

import java.time.LocalDate;

@Entity
@Table(name = "income_records")
public class IncomeRecord implements Record{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 35)
    private IncomeCategory category;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "description", length = 100)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public IncomeRecord() {
    }

    public IncomeRecord(IncomeCategory category,
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

    public IncomeCategory getCategory() {
        return category;
    }

    public void setCategory(IncomeCategory category) {
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
        return "INCOME";
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
