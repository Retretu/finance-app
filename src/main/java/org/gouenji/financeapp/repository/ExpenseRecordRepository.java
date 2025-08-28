package org.gouenji.financeapp.repository;

import org.gouenji.financeapp.entity.ExpenseRecord;
import org.gouenji.financeapp.entity.IncomeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ExpenseRecordRepository extends JpaRepository<ExpenseRecord, Integer> {

    @Query("SELECT i FROM ExpenseRecord i WHERE YEAR(i.date) = :year AND MONTH(i.date) = :month")
    List<ExpenseRecord> findAllByYearAndMonth(@Param("year") int year, @Param("month") int month);
}
