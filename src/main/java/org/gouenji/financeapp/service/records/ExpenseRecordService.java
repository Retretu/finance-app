package org.gouenji.financeapp.service.records;

import org.gouenji.financeapp.dto.ExpenseRecordsContainer;
import org.gouenji.financeapp.entity.ExpenseRecord;
import org.gouenji.financeapp.repository.ExpenseRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExpenseRecordService {
    private final ExpenseRecordRepository expenseRecordRepository;

    @Autowired
    public ExpenseRecordService(ExpenseRecordRepository expenseRecordRepository) {
        this.expenseRecordRepository = expenseRecordRepository;
    }

    @Transactional(readOnly = true)
    public ExpenseRecordsContainer findAll() {
        List<ExpenseRecord> expenseRecords = expenseRecordRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        double total = expenseRecords.stream().mapToDouble(ExpenseRecord::getAmount).sum();
        return new ExpenseRecordsContainer(expenseRecords, total);
    }


    public void deleteRecord(int id) {
        expenseRecordRepository.deleteById(id);
    }
}
