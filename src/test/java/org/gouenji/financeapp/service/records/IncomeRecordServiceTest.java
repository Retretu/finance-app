package org.gouenji.financeapp.service.records;

import jakarta.persistence.EntityNotFoundException;
import org.gouenji.financeapp.dto.records.IncomeRecordsContainer;
import org.gouenji.financeapp.entity.User;
import org.gouenji.financeapp.entity.enums.records.IncomeCategory;
import org.gouenji.financeapp.entity.enums.users.UserRole;
import org.gouenji.financeapp.entity.records.IncomeRecord;
import org.gouenji.financeapp.repository.IncomeRecordRepository;
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
public class IncomeRecordServiceTest {

    @Mock
    private IncomeRecordRepository incomeRecordRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private IncomeRecordService incomeRecordService;

    private User userTest;
    private IncomeRecord incomeRecordTest1;
    private IncomeRecord incomeRecordTest2;
    private IncomeRecord incomeRecordTest3;

    @BeforeEach
    void setUp() {
        userTest = new User(
                "Ivan",
                "test@gmail.com",
                "1234",
                UserRole.USER
        );
        userTest.setId(1);

        incomeRecordTest1 = new IncomeRecord(
                IncomeCategory.BONUS,
                125.0,
                LocalDate.now(),
                "Премия",
                userTest
        );
        incomeRecordTest1.setId(1);

        incomeRecordTest2 = new IncomeRecord(
                IncomeCategory.SALARY,
                300.0,
                LocalDate.now(),
                "Зп",
                userTest
        );
        incomeRecordTest2.setId(2);

        incomeRecordTest3 = new IncomeRecord(
                IncomeCategory.SALARY,
                300.0,
                LocalDate.now().minusDays(30),
                "Зп",
                userTest
        );
        incomeRecordTest2.setId(2);
    }

    @Test
    void findAll_ShouldReturnAllIncomeRecordsContainer_WhenFilterModeIsNull(){
        List<IncomeRecord> mockAllRecords = Arrays.asList(incomeRecordTest1, incomeRecordTest2, incomeRecordTest3);
        List<IncomeRecord> mockMonthRecords = Arrays.asList(incomeRecordTest1, incomeRecordTest2);
        when(userService.getCurrentUserId()).thenReturn(userTest.getId());
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(incomeRecordRepository.findAllByUserIdOrderByDateDesc(userTest.getId()))
                .thenReturn(mockAllRecords);
        when(incomeRecordRepository.findAllByYearAndMonthAndUser(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                userTest))
                .thenReturn(mockMonthRecords);

        double expectedTotal = incomeRecordTest1.getAmount() + incomeRecordTest2.getAmount() + incomeRecordTest3.getAmount();
        double expectedAverageTotal = (double) Math.round(expectedTotal / 12 * 100) / 100.0;
        double expectedMonthTotal = incomeRecordTest1.getAmount() + incomeRecordTest2.getAmount();


        IncomeRecordsContainer container = incomeRecordService.findAll(null);

        assertThat(container).isNotNull();
        assertThat(container.getRecords()).isEqualTo(mockAllRecords);
        assertThat(container.getTotal()).isEqualTo(expectedTotal);
        assertThat(container.getAverageTotal()).isEqualTo(expectedAverageTotal);
        assertThat(container.getMonthTotal()).isEqualTo(expectedMonthTotal);
        assertThat(container.getFilteredTotal()).isNull();
    }

    @Test
    void findAll_ShouldReturnAllIncomeRecordsContainer_WhenFilterModeIsEmpty() {
        List<IncomeRecord> mockAllRecords = Arrays.asList(incomeRecordTest1, incomeRecordTest2, incomeRecordTest3);
        List<IncomeRecord> mockMonthRecords = Arrays.asList(incomeRecordTest1, incomeRecordTest2);
        when(userService.getCurrentUserId()).thenReturn(userTest.getId());
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(incomeRecordRepository.findAllByUserIdOrderByDateDesc(userTest.getId()))
                .thenReturn(mockAllRecords);
        when(incomeRecordRepository.findAllByYearAndMonthAndUser(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                userTest))
                .thenReturn(mockMonthRecords);

        double expectedTotal = incomeRecordTest1.getAmount() + incomeRecordTest2.getAmount() + incomeRecordTest3.getAmount();
        double expectedAverageTotal = (double) Math.round(expectedTotal / 12 * 100) / 100.0;
        double expectedMonthTotal = incomeRecordTest1.getAmount() + incomeRecordTest2.getAmount();

        IncomeRecordsContainer container = incomeRecordService.findAll("");

        assertThat(container).isNotNull();
        assertThat(container.getRecords()).isEqualTo(mockAllRecords);
        assertThat(container.getTotal()).isEqualTo(expectedTotal);
        assertThat(container.getAverageTotal()).isEqualTo(expectedAverageTotal);
        assertThat(container.getMonthTotal()).isEqualTo(expectedMonthTotal);
        assertThat(container.getFilteredTotal()).isNull();
    }

    @Test
    void findAll_ShouldReturnAllRecordsContainer_WhenFilterModeIsAllCategories() {
        List<IncomeRecord> mockAllRecords = Arrays.asList(incomeRecordTest1, incomeRecordTest2, incomeRecordTest3);
        List<IncomeRecord> mockMonthRecords = Arrays.asList(incomeRecordTest1, incomeRecordTest2);
        when(userService.getCurrentUserId()).thenReturn(userTest.getId());
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(incomeRecordRepository.findAllByUserIdOrderByDateDesc(userTest.getId()))
                .thenReturn(mockAllRecords);
        when(incomeRecordRepository.findAllByYearAndMonthAndUser(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                userTest))
                .thenReturn(mockMonthRecords);

        double expectedTotal = incomeRecordTest1.getAmount() + incomeRecordTest2.getAmount() + incomeRecordTest3.getAmount();
        double expectedAverageTotal = (double) Math.round(expectedTotal / 12 * 100) / 100.0;
        double expectedMonthTotal = incomeRecordTest1.getAmount() + incomeRecordTest2.getAmount();

        IncomeRecordsContainer container = incomeRecordService.findAll("All categories");

        assertThat(container).isNotNull();
        assertThat(container.getRecords()).isEqualTo(mockAllRecords);
        assertThat(container.getTotal()).isEqualTo(expectedTotal);
        assertThat(container.getAverageTotal()).isEqualTo(expectedAverageTotal);
        assertThat(container.getMonthTotal()).isEqualTo(expectedMonthTotal);
        assertThat(container.getFilteredTotal()).isNull();
    }

    @Test
    void findAll_ShouldReturnFilteredRecordsContainer_WhenFilterModeIsValidCategory() {
        List<IncomeRecord> mockAllRecords = Arrays.asList(incomeRecordTest1, incomeRecordTest2, incomeRecordTest3);
        List<IncomeRecord> mockMonthRecords = Arrays.asList(incomeRecordTest1, incomeRecordTest2);
        when(userService.getCurrentUserId()).thenReturn(userTest.getId());
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(incomeRecordRepository.findAllByUserIdOrderByDateDesc(userTest.getId()))
                .thenReturn(mockAllRecords);
        when(incomeRecordRepository.findAllByYearAndMonthAndUser(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                userTest))
                .thenReturn(mockMonthRecords);

        List<IncomeRecord> expectedFilteredRecords = mockAllRecords.stream()
                .filter(record -> record.getCategory() == IncomeCategory.SALARY)
                .toList();
        double expectedFilteredTotal = expectedFilteredRecords.stream()
                .mapToDouble(IncomeRecord::getAmount)
                .sum();
        double expectedFilteredAverageTotal = Math.round(expectedFilteredTotal / 12 * 100) / 100.0;
        double expectedTotal = incomeRecordTest1.getAmount() + incomeRecordTest2.getAmount() + incomeRecordTest3.getAmount();
        double expectedMonthTotal = incomeRecordTest1.getAmount() + incomeRecordTest2.getAmount();

        IncomeRecordsContainer container = incomeRecordService.findAll("SALARY");

        assertThat(container).isNotNull();
        assertThat(container.getRecords()).isEqualTo(expectedFilteredRecords);
        assertThat(container.getTotal()).isEqualTo(expectedTotal);
        assertThat(container.getFilteredTotal()).isEqualTo(expectedFilteredTotal);
        assertThat(container.getAverageTotal()).isEqualTo(expectedFilteredAverageTotal);
        assertThat(container.getMonthTotal()).isEqualTo(expectedMonthTotal);
    }

    @Test
    void findAll_ShouldReturnAllRecordsContainer_WhenFilterModeIsInvalidCategory() {
        List<IncomeRecord> mockAllRecords = Arrays.asList(incomeRecordTest1, incomeRecordTest2, incomeRecordTest3);
        List<IncomeRecord> mockMonthRecords = Arrays.asList(incomeRecordTest1, incomeRecordTest2);
        when(userService.getCurrentUserId()).thenReturn(userTest.getId());
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(incomeRecordRepository.findAllByUserIdOrderByDateDesc(userTest.getId()))
                .thenReturn(mockAllRecords);
        when(incomeRecordRepository.findAllByYearAndMonthAndUser(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                userTest))
                .thenReturn(mockMonthRecords);

        double expectedTotal = incomeRecordTest1.getAmount() + incomeRecordTest2.getAmount() + incomeRecordTest3.getAmount();
        double expectedAverageTotal = (double) Math.round(expectedTotal / 12 * 100) / 100.0;
        double expectedMonthTotal = incomeRecordTest1.getAmount() + incomeRecordTest2.getAmount();

        IncomeRecordsContainer container = incomeRecordService.findAll("INVALID_CATEGORY");

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
        when(incomeRecordRepository.findAllByUserIdOrderByDateDesc(userTest.getId()))
                .thenReturn(Arrays.asList());
        when(incomeRecordRepository.findAllByYearAndMonthAndUser(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                userTest))
                .thenReturn(Arrays.asList());

        IncomeRecordsContainer container = incomeRecordService.findAll(null);


        assertThat(container).isNotNull();
        assertThat(container.getRecords()).isEmpty();
        assertThat(container.getTotal()).isEqualTo(0.0);
        assertThat(container.getAverageTotal()).isEqualTo(0.0);
        assertThat(container.getMonthTotal()).isEqualTo(0.0);
        assertThat(container.getFilteredTotal()).isNull();
    }


    @Test
    void findRecord_ShouldReturnIncomeRecord_WhenRecordExists() {
        when(incomeRecordRepository.findById(incomeRecordTest1.getId()))
                .thenReturn(Optional.of(incomeRecordTest1));

        IncomeRecord incomeRecord = incomeRecordService.findRecord(incomeRecordTest1.getId());

        assertThat(incomeRecord).isEqualTo(incomeRecordTest1);
        verify(incomeRecordRepository).findById(incomeRecordTest1.getId());
    }

    @Test
    void findRecord_ShouldThrowException_WhenRecordDoesNotExist() {
        when(incomeRecordRepository.findById(incomeRecordTest2.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> incomeRecordService.findRecord(incomeRecordTest2.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                        .hasMessage("Income record not found with id: " + incomeRecordTest2.getId());
        verify(incomeRecordRepository).findById(incomeRecordTest2.getId());
    }

    @Test
    void saveRecord_ShouldCallIncomeRepository() {

        when(userService.getCurrentUser()).thenReturn(userTest);

        incomeRecordService.saveRecord(incomeRecordTest1.getCategory(),
                incomeRecordTest1.getAmount(),
                incomeRecordTest1.getDate(),
                incomeRecordTest1.getDescription());

        verify(incomeRecordRepository).save(any(IncomeRecord.class));
    }

    @Test
    void updateRecord_ShouldUpdateFieldsAndSave_WhenRecordExists() {

        when(incomeRecordRepository.findById(incomeRecordTest1.getId()))
                .thenReturn(Optional.of(incomeRecordTest1));

        incomeRecordService.updateRecord(
                incomeRecordTest1.getId(),
                incomeRecordTest2.getCategory(),
                incomeRecordTest2.getAmount(),
                incomeRecordTest2.getDate(),
                incomeRecordTest2.getDescription()
        );

        assertThat(incomeRecordTest1.getCategory()).isEqualTo(incomeRecordTest2.getCategory());
        assertThat(incomeRecordTest1.getAmount()).isEqualTo(incomeRecordTest2.getAmount());
        assertThat(incomeRecordTest1.getDate()).isEqualTo(incomeRecordTest2.getDate());
        assertThat(incomeRecordTest1.getDescription()).isEqualTo(incomeRecordTest2.getDescription());

        verify(incomeRecordRepository).findById(incomeRecordTest1.getId());
        verify(incomeRecordRepository).save(incomeRecordTest1);

    }

    @Test
    void updateRecord_ShouldThrowException_WhenRecordDoesNotExist() {
        when(incomeRecordRepository.findById(incomeRecordTest2.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> incomeRecordService.updateRecord(
                        incomeRecordTest2.getId(),
                        incomeRecordTest2.getCategory(),
                        incomeRecordTest2.getAmount(),
                        incomeRecordTest2.getDate(),
                        incomeRecordTest2.getDescription()
                ))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Income record not found with id: " + incomeRecordTest2.getId());
        verify(incomeRecordRepository).findById(incomeRecordTest2.getId());
    }

    @Test
    void deleteRecord_ShouldCallIncomeRepository() {
        incomeRecordService.deleteRecord(incomeRecordTest1.getId());

        verify(incomeRecordRepository).deleteById(incomeRecordTest1.getId());
    }

}

