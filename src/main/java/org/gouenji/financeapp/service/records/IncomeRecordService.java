package org.gouenji.financeapp.service.records;

import org.gouenji.financeapp.entity.records.IncomeRecord;
import org.gouenji.financeapp.dto.records.IncomeRecordsContainer;
import org.gouenji.financeapp.entity.enums.records.IncomeCategory;
import org.gouenji.financeapp.repository.IncomeRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class IncomeRecordService {
    private final IncomeRecordRepository incomeRecordRepository;

    @Autowired
    public IncomeRecordService(IncomeRecordRepository incomeRecordRepository) {
        this.incomeRecordRepository = incomeRecordRepository;
    }

    @Transactional(readOnly = true)
    public IncomeRecordsContainer findAll(String filterMode) {
        List<IncomeRecord> incomeRecords = incomeRecordRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        double total = incomeRecords.stream()
                .mapToDouble(IncomeRecord::getAmount)
                .sum();
        double averageTotal = (double) Math.round( total / 12 * 100) / 100;
        double monthTotal = incomeRecordRepository
                .findAllByYearAndMonth(LocalDate.now().getYear(), LocalDate.now().getMonthValue()).stream()
                .mapToDouble(IncomeRecord::getAmount)
                .sum();
        if(filterMode == null || filterMode.isEmpty() || filterMode.equals("All categories")) {
            return IncomeRecordsContainer.builder()
                    .records(incomeRecords)
                    .total(total)
                    .averageTotal(averageTotal)
                    .monthTotal(monthTotal)
                    .build();
        }
        List<String> allModes = Arrays.stream(IncomeCategory.values())
                .map(Enum::name)
                .toList();
        if(allModes.contains(filterMode)) {
            List<IncomeRecord> filteredIncomeRecords =  incomeRecords.stream()
                    .filter(record -> record.getCategory() == IncomeCategory.valueOf(filterMode))
                    .toList();
            double filteredTotal = filteredIncomeRecords.stream()
                    .mapToDouble(IncomeRecord::getAmount)
                    .sum();
            double filteredAverageTotal = (double) Math.round( filteredTotal / 12 * 100) / 100;
            return IncomeRecordsContainer.builder()
                    .records(filteredIncomeRecords)
                    .total(total)
                    .filteredTotal(filteredTotal)
                    .averageTotal(filteredAverageTotal)
                    .monthTotal(monthTotal)
                    .build();
        }else{
            return IncomeRecordsContainer.builder()
                    .records(incomeRecords)
                    .total(total)
                    .averageTotal(averageTotal)
                    .monthTotal(monthTotal)
                    .build();
        }
    }

    public void saveRecord(IncomeCategory category, double amount, LocalDate date, String description) {
        incomeRecordRepository.save(new IncomeRecord(category, amount, date, description));
    }

    public void deleteRecord(int id) {
        incomeRecordRepository.deleteById(id);
    }
}
