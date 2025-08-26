package org.gouenji.financeapp.repository;

import org.gouenji.financeapp.entity.ExpenseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ExpenseRecordRepository extends JpaRepository<ExpenseRecord, Integer> {

}
