package org.gouenji.financeapp.service.records;

import org.gouenji.financeapp.dto.ExpenseRecordsContainer;
import org.gouenji.financeapp.entity.records.ExpenseRecord;
import org.gouenji.financeapp.entity.enums.records.ExpenseCategory;
import org.gouenji.financeapp.repository.ExpenseRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
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
    public ExpenseRecordsContainer findAll(String filterMode) {
        List<ExpenseRecord> expenseRecords = expenseRecordRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        double total = expenseRecords.stream()
                .mapToDouble(ExpenseRecord::getAmount)
                .sum();
        double averageTotal = (double) Math.round(total / 12 * 100) / 100;
        double monthTotal = expenseRecordRepository
                .findAllByYearAndMonth(LocalDate.now().getYear(), LocalDate.now().getMonthValue()).stream()
                .mapToDouble(ExpenseRecord::getAmount)
                .sum();
        if(filterMode == null || filterMode.isEmpty() || filterMode.equals("All categories")){
            return ExpenseRecordsContainer.builder()
                    .records(expenseRecords)
                    .total(total)
                    .averageTotal(averageTotal)
                    .monthTotal(monthTotal)
                    .build();
        }
        List<String> allModes = Arrays.stream(ExpenseCategory.values())
                .map(Enum::name)
                .toList();
        if(allModes.contains(filterMode)){
            List<ExpenseRecord> filteredExpenseRecords = expenseRecords.stream()
                    .filter(record -> record.getCategory() == ExpenseCategory.valueOf(filterMode))
                    .toList();
            double filteredTotal = filteredExpenseRecords.stream()
                    .mapToDouble(ExpenseRecord::getAmount)
                    .sum();
            double filteredAverageTotal = (double) Math.round(filteredTotal / 12 * 100) / 100;
            return ExpenseRecordsContainer.builder()
                    .records(filteredExpenseRecords)
                    .total(total)
                    .filteredTotal(filteredTotal)
                    .averageTotal(filteredAverageTotal)
                    .monthTotal(monthTotal)
                    .build();
        }else{
            return ExpenseRecordsContainer.builder()
                    .records(expenseRecords)
                    .total(total)
                    .averageTotal(averageTotal)
                    .monthTotal(monthTotal)
                    .build();
        }
    }

    public void saveRecord(ExpenseCategory category, double amount, LocalDate date, String description){
        expenseRecordRepository.save(new ExpenseRecord(category, amount, date, description));
    }


    public void deleteRecord(int id) {
        expenseRecordRepository.deleteById(id);
    }
}
