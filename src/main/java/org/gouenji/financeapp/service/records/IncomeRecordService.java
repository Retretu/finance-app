package org.gouenji.financeapp.service.records;

import jakarta.persistence.EntityNotFoundException;
import org.gouenji.financeapp.entity.records.IncomeRecord;
import org.gouenji.financeapp.dto.records.IncomeRecordsContainer;
import org.gouenji.financeapp.entity.enums.records.IncomeCategory;
import org.gouenji.financeapp.repository.IncomeRecordRepository;
import org.gouenji.financeapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class IncomeRecordService {
    private final IncomeRecordRepository incomeRecordRepository;
    private final UserService userService;

    @Autowired
    public IncomeRecordService(IncomeRecordRepository incomeRecordRepository, UserService userService) {
        this.incomeRecordRepository = incomeRecordRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public IncomeRecordsContainer findAll(String filterMode) {
        List<IncomeRecord> incomeRecords = incomeRecordRepository
                .findAllByUserIdOrderByDateDesc(userService.getCurrentUserId());
        double total = incomeRecords.stream()
                .mapToDouble(IncomeRecord::getAmount)
                .sum();
        double averageTotal = (double) Math.round( total / 12 * 100) / 100;
        double monthTotal = incomeRecordRepository
                .findAllByYearAndMonthAndUser(LocalDate.now().getYear(),
                        LocalDate.now().getMonthValue(), userService.getCurrentUser()).stream()
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

    public IncomeRecord findRecord(int id){
        return incomeRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Income record not found with id: " + id));
    }

    public void saveRecord(IncomeCategory category,
                           double amount,
                           LocalDate date,
                           String description) {
        incomeRecordRepository.save(new IncomeRecord(category, amount, date, description, userService.getCurrentUser()));
    }

    public void updateRecord(int id,
                             IncomeCategory category,
                             double amount,
                             LocalDate date,
                             String description){
        IncomeRecord record = findRecord(id);

        record.setCategory(category);
        record.setAmount(amount);
        record.setDate(date);
        record.setDescription(description);

        incomeRecordRepository.save(record);
    }

    public void deleteRecord(int id) {
        incomeRecordRepository.deleteById(id);
    }
}
