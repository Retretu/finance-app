package org.gouenji.financeapp.repository;

import org.gouenji.financeapp.entity.User;
import org.gouenji.financeapp.entity.records.IncomeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IncomeRecordRepository extends JpaRepository<IncomeRecord, Integer> {

    @Query("SELECT i FROM IncomeRecord i WHERE YEAR(i.date) = :year AND MONTH(i.date) = :month AND i.user = :user")
    List<IncomeRecord> findAllByYearAndMonthAndUser(@Param("year") int year,
                                                    @Param("month") int month,
                                                    @Param("user") User user);

    List<IncomeRecord> findAllByUserIdOrderByDateDesc(int userId);
}
