package org.gouenji.financeapp.service.records;

import jakarta.persistence.EntityNotFoundException;
import org.gouenji.financeapp.dto.records.ExpenseRecordsContainer;
import org.gouenji.financeapp.entity.User;
import org.gouenji.financeapp.entity.enums.records.ExpenseCategory;
import org.gouenji.financeapp.entity.enums.users.UserRole;
import org.gouenji.financeapp.entity.records.ExpenseRecord;
import org.gouenji.financeapp.repository.ExpenseRecordRepository;
import org.gouenji.financeapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ExpenseRecordServiceTest {

    @Mock
    private ExpenseRecordRepository expenseRecordRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ExpenseRecordService expenseRecordService;

    private User userTest;
    private ExpenseRecord expenseRecordTest1;
    private ExpenseRecord expenseRecordTest2;
    private ExpenseRecord expenseRecordTest3;

    @BeforeEach
    void setUp() {
        userTest = new User(
                "Ivan",
                "test@gmail.com",
                "1234",
                UserRole.USER
        );
        userTest.setId(1);

        expenseRecordTest1 = new ExpenseRecord(
                ExpenseCategory.FUN,
                15.0,
                LocalDate.now(),
                "Парк",
                userTest
        );
        expenseRecordTest1.setId(1);

        expenseRecordTest2 = new ExpenseRecord(
                ExpenseCategory.FUN,
                30.0,
                LocalDate.now(),
                "Прогулка",
                userTest
        );
        expenseRecordTest2.setId(2);

        expenseRecordTest3 = new ExpenseRecord(
                ExpenseCategory.FOOD,
                5.0,
                LocalDate.now().minusDays(30),
                "Хот дог",
                userTest
        );
        expenseRecordTest2.setId(2);
    }

    @Test
    void findAll_ShouldReturnAllExpenseRecordsContainer_WhenFilterModeIsNull(){
        List<ExpenseRecord> mockAllRecords = Arrays.asList(expenseRecordTest1, expenseRecordTest2, expenseRecordTest3);
        List<ExpenseRecord> mockMonthRecords = Arrays.asList(expenseRecordTest1, expenseRecordTest2);
        when(userService.getCurrentUserId()).thenReturn(userTest.getId());
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(expenseRecordRepository.findAllByUserIdOrderByDateDesc(userTest.getId()))
                .thenReturn(mockAllRecords);
        when(expenseRecordRepository.findAllByYearAndMonthAndUser(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                userTest))
                .thenReturn(mockMonthRecords);

        double expectedTotal = expenseRecordTest1.getAmount() + expenseRecordTest2.getAmount() + expenseRecordTest3.getAmount();
        double expectedAverageTotal = (double) Math.round(expectedTotal / 12 * 100) / 100.0;
        double expectedMonthTotal = expenseRecordTest1.getAmount() + expenseRecordTest2.getAmount();


        ExpenseRecordsContainer container = expenseRecordService.findAll(null);

        assertThat(container).isNotNull();
        assertThat(container.getRecords()).isEqualTo(mockAllRecords);
        assertThat(container.getTotal()).isEqualTo(expectedTotal);
        assertThat(container.getAverageTotal()).isEqualTo(expectedAverageTotal);
        assertThat(container.getMonthTotal()).isEqualTo(expectedMonthTotal);
        assertThat(container.getFilteredTotal()).isNull();
    }

    @Test
    void findAll_ShouldReturnAllExpenseRecordsContainer_WhenFilterModeIsEmpty() {
        List<ExpenseRecord> mockAllRecords = Arrays.asList(expenseRecordTest1, expenseRecordTest2, expenseRecordTest3);
        List<ExpenseRecord> mockMonthRecords = Arrays.asList(expenseRecordTest1, expenseRecordTest2);
        when(userService.getCurrentUserId()).thenReturn(userTest.getId());
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(expenseRecordRepository.findAllByUserIdOrderByDateDesc(userTest.getId()))
                .thenReturn(mockAllRecords);
        when(expenseRecordRepository.findAllByYearAndMonthAndUser(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                userTest))
                .thenReturn(mockMonthRecords);

        double expectedTotal = expenseRecordTest1.getAmount() + expenseRecordTest2.getAmount() + expenseRecordTest3.getAmount();
        double expectedAverageTotal = (double) Math.round(expectedTotal / 12 * 100) / 100.0;
        double expectedMonthTotal = expenseRecordTest1.getAmount() + expenseRecordTest2.getAmount();

        ExpenseRecordsContainer container = expenseRecordService.findAll("");

        assertThat(container).isNotNull();
        assertThat(container.getRecords()).isEqualTo(mockAllRecords);
        assertThat(container.getTotal()).isEqualTo(expectedTotal);
        assertThat(container.getAverageTotal()).isEqualTo(expectedAverageTotal);
        assertThat(container.getMonthTotal()).isEqualTo(expectedMonthTotal);
        assertThat(container.getFilteredTotal()).isNull();
    }

    @Test
    void findAll_ShouldReturnAllExpenseRecordsContainer_WhenFilterModeIsAllCategories() {
        List<ExpenseRecord> mockAllRecords = Arrays.asList(expenseRecordTest1, expenseRecordTest2, expenseRecordTest3);
        List<ExpenseRecord> mockMonthRecords = Arrays.asList(expenseRecordTest1, expenseRecordTest2);
        when(userService.getCurrentUserId()).thenReturn(userTest.getId());
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(expenseRecordRepository.findAllByUserIdOrderByDateDesc(userTest.getId()))
                .thenReturn(mockAllRecords);
        when(expenseRecordRepository.findAllByYearAndMonthAndUser(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                userTest))
                .thenReturn(mockMonthRecords);

        double expectedTotal = expenseRecordTest1.getAmount() + expenseRecordTest2.getAmount() + expenseRecordTest3.getAmount();
        double expectedAverageTotal = (double) Math.round(expectedTotal / 12 * 100) / 100.0;
        double expectedMonthTotal = expenseRecordTest1.getAmount() + expenseRecordTest2.getAmount();

        ExpenseRecordsContainer container = expenseRecordService.findAll("All categories");

        assertThat(container).isNotNull();
        assertThat(container.getRecords()).isEqualTo(mockAllRecords);
        assertThat(container.getTotal()).isEqualTo(expectedTotal);
        assertThat(container.getAverageTotal()).isEqualTo(expectedAverageTotal);
        assertThat(container.getMonthTotal()).isEqualTo(expectedMonthTotal);
        assertThat(container.getFilteredTotal()).isNull();
    }

    @Test
    void findAll_ShouldReturnFilteredExpenseRecordsContainer_WhenFilterModeIsValidCategory() {
        List<ExpenseRecord> mockAllRecords = Arrays.asList(expenseRecordTest1, expenseRecordTest2, expenseRecordTest3);
        List<ExpenseRecord> mockMonthRecords = Arrays.asList(expenseRecordTest1, expenseRecordTest2);
        when(userService.getCurrentUserId()).thenReturn(userTest.getId());
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(expenseRecordRepository.findAllByUserIdOrderByDateDesc(userTest.getId()))
                .thenReturn(mockAllRecords);
        when(expenseRecordRepository.findAllByYearAndMonthAndUser(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                userTest))
                .thenReturn(mockMonthRecords);

        List<ExpenseRecord> expectedFilteredRecords = mockAllRecords.stream()
                .filter(record -> record.getCategory() == ExpenseCategory.FUN)
                .toList();
        double expectedFilteredTotal = expectedFilteredRecords.stream()
                .mapToDouble(ExpenseRecord::getAmount)
                .sum();
        double expectedFilteredAverageTotal = Math.round(expectedFilteredTotal / 12 * 100) / 100.0;
        double expectedTotal = expenseRecordTest1.getAmount() + expenseRecordTest2.getAmount() + expenseRecordTest3.getAmount();
        double expectedMonthTotal = expenseRecordTest1.getAmount() + expenseRecordTest2.getAmount();

        ExpenseRecordsContainer container = expenseRecordService.findAll("FUN");

        assertThat(container).isNotNull();
        assertThat(container.getRecords()).isEqualTo(expectedFilteredRecords);
        assertThat(container.getTotal()).isEqualTo(expectedTotal);
        assertThat(container.getFilteredTotal()).isEqualTo(expectedFilteredTotal);
        assertThat(container.getAverageTotal()).isEqualTo(expectedFilteredAverageTotal);
        assertThat(container.getMonthTotal()).isEqualTo(expectedMonthTotal);
    }

    @Test
    void findAll_ShouldReturnAllExpenseRecordsContainer_WhenFilterModeIsInvalidCategory() {
        List<ExpenseRecord> mockAllRecords = Arrays.asList(expenseRecordTest1, expenseRecordTest2, expenseRecordTest3);
        List<ExpenseRecord> mockMonthRecords = Arrays.asList(expenseRecordTest1, expenseRecordTest2);
        when(userService.getCurrentUserId()).thenReturn(userTest.getId());
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(expenseRecordRepository.findAllByUserIdOrderByDateDesc(userTest.getId()))
                .thenReturn(mockAllRecords);
        when(expenseRecordRepository.findAllByYearAndMonthAndUser(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                userTest))
                .thenReturn(mockMonthRecords);

        double expectedTotal = expenseRecordTest1.getAmount() + expenseRecordTest2.getAmount() + expenseRecordTest3.getAmount();
        double expectedAverageTotal = (double) Math.round(expectedTotal / 12 * 100) / 100.0;
        double expectedMonthTotal = expenseRecordTest1.getAmount() + expenseRecordTest2.getAmount();

        ExpenseRecordsContainer container = expenseRecordService.findAll("INVALID_CATEGORY");

        assertThat(container).isNotNull();
        assertThat(container.getRecords()).isEqualTo(mockAllRecords);
        assertThat(container.getTotal()).isEqualTo(expectedTotal);
        assertThat(container.getAverageTotal()).isEqualTo(expectedAverageTotal);
        assertThat(container.getMonthTotal()).isEqualTo(expectedMonthTotal);
        assertThat(container.getFilteredTotal()).isNull();
    }

    @Test
    void findAll_ShouldReturnEmptyContainer_WhenNoRecordsExist() {
        when(userService.getCurrentUserId()).thenReturn(userTest.getId());
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(expenseRecordRepository.findAllByUserIdOrderByDateDesc(userTest.getId()))
                .thenReturn(List.of());
        when(expenseRecordRepository.findAllByYearAndMonthAndUser(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                userTest))
                .thenReturn(List.of());

        ExpenseRecordsContainer container = expenseRecordService.findAll(null);


        assertThat(container).isNotNull();
        assertThat(container.getRecords()).isEmpty();
        assertThat(container.getTotal()).isEqualTo(0.0);
        assertThat(container.getAverageTotal()).isEqualTo(0.0);
        assertThat(container.getMonthTotal()).isEqualTo(0.0);
        assertThat(container.getFilteredTotal()).isNull();
    }


    @Test
    void findRecord_ShouldReturnExpenseRecord_WhenRecordExists() {
        when(expenseRecordRepository.findById(expenseRecordTest1.getId()))
                .thenReturn(Optional.of(expenseRecordTest1));

        ExpenseRecord expenseRecord = expenseRecordService.findRecord(expenseRecordTest1.getId());

        assertThat(expenseRecord).isEqualTo(expenseRecordTest1);
        verify(expenseRecordRepository).findById(expenseRecordTest1.getId());
    }

    @Test
    void findRecord_ShouldThrowException_WhenRecordDoesNotExist() {
        when(expenseRecordRepository.findById(expenseRecordTest2.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseRecordService.findRecord(expenseRecordTest2.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Expense record not found with id: " + expenseRecordTest2.getId());
        verify(expenseRecordRepository).findById(expenseRecordTest2.getId());
    }

    @Test
    void saveRecord_ShouldCallExpenseRepository() {

        when(userService.getCurrentUser()).thenReturn(userTest);

        expenseRecordService.saveRecord(expenseRecordTest1.getCategory(),
                expenseRecordTest1.getAmount(),
                expenseRecordTest1.getDate(),
                expenseRecordTest1.getDescription());

        verify(expenseRecordRepository).save(any(ExpenseRecord.class));
    }

    @Test
    void updateRecord_ShouldUpdateFieldsAndSave_WhenRecordExists() {

        when(expenseRecordRepository.findById(expenseRecordTest1.getId()))
                .thenReturn(Optional.of(expenseRecordTest1));

        expenseRecordService.updateRecord(
                expenseRecordTest1.getId(),
                expenseRecordTest2.getCategory(),
                expenseRecordTest2.getAmount(),
                expenseRecordTest2.getDate(),
                expenseRecordTest2.getDescription()
        );

        assertThat(expenseRecordTest1.getCategory()).isEqualTo(expenseRecordTest2.getCategory());
        assertThat(expenseRecordTest1.getAmount()).isEqualTo(expenseRecordTest2.getAmount());
        assertThat(expenseRecordTest1.getDate()).isEqualTo(expenseRecordTest2.getDate());
        assertThat(expenseRecordTest1.getDescription()).isEqualTo(expenseRecordTest2.getDescription());

        verify(expenseRecordRepository).findById(expenseRecordTest1.getId());
        verify(expenseRecordRepository).save(expenseRecordTest1);

    }

    @Test
    void updateRecord_ShouldThrowException_WhenRecordDoesNotExist() {
        when(expenseRecordRepository.findById(expenseRecordTest2.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseRecordService.updateRecord(
                expenseRecordTest2.getId(),
                expenseRecordTest2.getCategory(),
                expenseRecordTest2.getAmount(),
                expenseRecordTest2.getDate(),
                expenseRecordTest2.getDescription()
        ))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Expense record not found with id: " + expenseRecordTest2.getId());
        verify(expenseRecordRepository).findById(expenseRecordTest2.getId());
    }

    @Test
    void deleteRecord_ShouldCallIncomeRepository() {
        expenseRecordService.deleteRecord(expenseRecordTest1.getId());

        verify(expenseRecordRepository).deleteById(expenseRecordTest1.getId());
    }

}

