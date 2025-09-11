package org.gouenji.financeapp.controller.secured;

import org.gouenji.financeapp.dto.records.ExpenseRecordsContainer;
import org.gouenji.financeapp.dto.records.IncomeRecordsContainer;
import org.gouenji.financeapp.entity.User;
import org.gouenji.financeapp.entity.enums.records.ExpenseCategory;
import org.gouenji.financeapp.entity.enums.records.IncomeCategory;
import org.gouenji.financeapp.entity.enums.users.UserRole;
import org.gouenji.financeapp.entity.records.ExpenseRecord;
import org.gouenji.financeapp.entity.records.IncomeRecord;
import org.gouenji.financeapp.service.records.ExpenseRecordService;
import org.gouenji.financeapp.service.records.IncomeRecordService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PrivateAccountControllerTest {

    @Mock
    private IncomeRecordService incomeRecordService;

    @Mock
    private ExpenseRecordService expenseRecordService;

    @InjectMocks
    private PrivateAccountController privateAccountController;

    private MockMvc mockMvc;

    private User userTest;
    private ExpenseRecordsContainer expenseRecordsContainer;
    private IncomeRecordsContainer incomeRecordsContainer;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(privateAccountController)
                .build();
        userTest = new User(
                "Ivan",
                "test@gmail.com",
                "1234",
                UserRole.USER
        );
        userTest.setId(1);


        IncomeRecord incomeRecord1 = new IncomeRecord(
                IncomeCategory.SALARY,
                100.0,
                LocalDate.of(2025, 1, 10),
                "Зарплата",
                userTest
        );

        IncomeRecord incomeRecord2 = new IncomeRecord(
                IncomeCategory.BONUS,
                25.0,
                LocalDate.of(2025, 2, 12),
                "Премия",
                userTest
        );

        ExpenseRecord expenseRecord1 = new ExpenseRecord(
                ExpenseCategory.FOOD,
                5.0,
                LocalDate.of(2025, 1, 11),
                "Хот дог",
                userTest
        );

        incomeRecordsContainer = IncomeRecordsContainer.builder()
                .records(List.of(incomeRecord1, incomeRecord2))
                .total(incomeRecord1.getAmount() + incomeRecord2.getAmount()) // 125
                .averageTotal((double) Math.round((double) (100 + 25) / 12 * 100) / 100) // 10.42
                .monthTotal(incomeRecord1.getAmount()) // 100
                .build();

        expenseRecordsContainer = ExpenseRecordsContainer.builder()
                .records(List.of(expenseRecord1))
                .total(expenseRecord1.getAmount()) // 5
                .monthTotal(expenseRecord1.getAmount()) // 5
                .averageTotal((double) Math.round(expenseRecord1.getAmount() / 12 * 100) / 100) // 5
                .monthTotal(expenseRecord1.getAmount()) // 5
                .build();
    }

    // === Тесты для GET /account

    @Test
    void getMainPage_ShouldReturnAccountPageWithCorrectModel() throws Exception{

        when(incomeRecordService.findAll(any())).thenReturn(incomeRecordsContainer);
        when(expenseRecordService.findAll(any())).thenReturn(expenseRecordsContainer);

        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(view().name("private/account-page"))
                .andExpect(model().attribute("totalIncome", 125.0))
                .andExpect(model().attribute("totalExpense", 5.0))
                .andExpect(model().attribute("totalBalance", 120.0))
                .andExpect(model().attribute("monthIncome", 100.0))
                .andExpect(model().attribute("monthExpense", 5.0))
                .andExpect(model().attribute("recentTransactionsCount", 3))
                .andExpect(model().attributeExists("recentTransactions"));
    }

    @Test
    void getMainPage_ShouldReturnZeroes_WhenNoRecords() throws Exception {
        IncomeRecordsContainer emptyIncome = IncomeRecordsContainer.builder()
                .records(Collections.emptyList())
                .total(0.0)
                .averageTotal(0.0)
                .monthTotal(0.0)
                .build();

        ExpenseRecordsContainer emptyExpense = ExpenseRecordsContainer.builder()
                .records(Collections.emptyList())
                .total(0.0)
                .averageTotal(0.0)
                .monthTotal(0.0)
                .build();

        when(incomeRecordService.findAll(any())).thenReturn(emptyIncome);
        when(expenseRecordService.findAll(any())).thenReturn(emptyExpense);

        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(view().name("private/account-page"))
                .andExpect(model().attribute("totalIncome", 0.0))
                .andExpect(model().attribute("totalExpense", 0.0))
                .andExpect(model().attribute("totalBalance", 0.0))
                .andExpect(model().attribute("monthIncome", 0.0))
                .andExpect(model().attribute("monthExpense", 0.0))
                .andExpect(model().attribute("recentTransactionsCount", 0))
                .andExpect(model().attribute("recentTransactions", Matchers.empty()));
    }

    @Test
    void editTransaction_ShouldRedirectToIncomeEdit() throws Exception {
        mockMvc.perform(post("/account/transaction/edit/1")
                        .param("type", "INCOME"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/income/edit/1"));
    }

    @Test
    void editTransaction_ShouldRedirectToExpenseEdit() throws Exception {
        mockMvc.perform(post("/account/transaction/edit/1")
                        .param("type", "EXPENSE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/expense/edit/1"));
    }

    @Test
    void editTransaction_ShouldRedirectToErrorOnUnknownType() throws Exception {
        mockMvc.perform(post("/account/transaction/edit/1")
                        .param("type", "UNKNOWN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/error"));
    }

    @Test
    void deleteTransaction_ShouldDeleteIncomeAndRedirect() throws Exception {
        mockMvc.perform(post("/account/transaction/delete/1")
                        .param("type", "INCOME"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"));
        verify(incomeRecordService).deleteRecord(1);
    }

    @Test
    void deleteTransaction_ShouldDeleteExpenseAndRedirect() throws Exception {
        mockMvc.perform(post("/account/transaction/delete/1")
                        .param("type", "EXPENSE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"));
        verify(expenseRecordService).deleteRecord(1);
    }

    @Test
    void deleteTransaction_ShouldRedirectToErrorOnUnknownType() throws Exception {
        mockMvc.perform(post("/account/transaction/delete/1")
                        .param("type", "UNKNOWN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/error"));
        verifyNoInteractions(incomeRecordService, expenseRecordService);
    }

    @Test
    void getIncomePage_ShouldReturnIncomePageWithCorrectModel() throws Exception {
        when(incomeRecordService.findAll(any())).thenReturn(incomeRecordsContainer);

        mockMvc.perform(get("/account/income"))
                .andExpect(status().isOk())
                .andExpect(view().name("private/income/income-page"))
                .andExpect(model().attribute("incomeRecords", incomeRecordsContainer.getRecords()))
                .andExpect(model().attribute("totalIncome", incomeRecordsContainer.getTotal()))
                .andExpect(model().attribute("filteredTotalIncome", incomeRecordsContainer.getTotal()))
                .andExpect(model().attributeExists("incomeCategories"))
                .andExpect(model().attribute("averageIncome", incomeRecordsContainer.getAverageTotal()))
                .andExpect(model().attribute("monthTotal", incomeRecordsContainer.getMonthTotal()));
    }

    @Test
    void addIncomeRecord_ShouldSaveRecordAndRedirect() throws Exception {
        mockMvc.perform(post("/account/income/add")
                        .param("category", IncomeCategory.SALARY.name())
                        .param("amount", "200.0")
                        .param("date", "2025-01-01")
                        .param("description", "Test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/income"));

        verify(incomeRecordService).saveRecord(
                eq(IncomeCategory.SALARY), eq(200.0),
                eq(LocalDate.of(2025,1,1)), eq("Test"));
    }
    @Test
    void deleteIncomeRecord_ShouldDeleteAndRedirect() throws Exception {
        mockMvc.perform(post("/account/income/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/income"));
        verify(incomeRecordService).deleteRecord(1);
    }
    @Test
    void getIncomeEditPage_ShouldReturnEditPageWithRecord() throws Exception {
        IncomeRecord record = new IncomeRecord(IncomeCategory.SALARY, 100.0,
                LocalDate.now(), "desc", userTest);
        when(incomeRecordService.findRecord(1)).thenReturn(record);

        mockMvc.perform(get("/account/income/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("private/income/income-edit-page"))
                .andExpect(model().attribute("income", record))
                .andExpect(model().attributeExists("incomeCategories"));
    }
    @Test
    void editIncomeRecord_ShouldUpdateAndRedirect() throws Exception {
        mockMvc.perform(post("/account/income/edit/1")
                        .param("category", IncomeCategory.SALARY.name())
                        .param("amount", "300.0")
                        .param("date", "2025-02-01")
                        .param("description", "updated"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/income"));

        verify(incomeRecordService).updateRecord(1,
                IncomeCategory.SALARY, 300.0,
                LocalDate.of(2025,2,1),"updated");
    }

    @Test
    void getExpensePage_ShouldReturnExpensePageWithCorrectModel() throws Exception {
        when(expenseRecordService.findAll(any())).thenReturn(expenseRecordsContainer);

        mockMvc.perform(get("/account/expense"))
                .andExpect(status().isOk())
                .andExpect(view().name("private/expense/expense-page"))
                .andExpect(model().attribute("expenseRecords", expenseRecordsContainer.getRecords()))
                .andExpect(model().attribute("totalExpense", expenseRecordsContainer.getTotal()))
                .andExpect(model().attribute("filteredTotalExpense", expenseRecordsContainer.getTotal()))
                .andExpect(model().attributeExists("expenseCategories"))
                .andExpect(model().attribute("averageExpense", expenseRecordsContainer.getAverageTotal()))
                .andExpect(model().attribute("monthTotal", expenseRecordsContainer.getMonthTotal()));
    }
    @Test
    void getExpenseAddPage_ShouldReturnAddPageWithCategories() throws Exception {
        mockMvc.perform(get("/account/expense/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("private/expense/expense-add-page"))
                .andExpect(model().attributeExists("expenseCategories"));
    }
    @Test
    void addExpenseRecord_ShouldSaveRecordAndRedirect() throws Exception {
        mockMvc.perform(post("/account/expense/add")
                        .param("category", ExpenseCategory.FOOD.name())
                        .param("amount", "50.0")
                        .param("date", "2025-03-01")
                        .param("description", "Test expense"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/expense"));

        verify(expenseRecordService).saveRecord(
                eq(ExpenseCategory.FOOD), eq(50.0),
                eq(LocalDate.of(2025,3,1)), eq("Test expense"));
    }
    @Test
    void deleteExpenseRecord_ShouldDeleteAndRedirect() throws Exception {
        mockMvc.perform(post("/account/expense/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/expense"));
        verify(expenseRecordService).deleteRecord(1);
    }
    @Test
    void getExpenseEditPage_ShouldReturnEditPageWithRecord() throws Exception {
        ExpenseRecord record = new ExpenseRecord(ExpenseCategory.FOOD, 10.0,
                LocalDate.now(), "desc", userTest);
        when(expenseRecordService.findRecord(1)).thenReturn(record);

        mockMvc.perform(get("/account/expense/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("private/expense/expense-edit-page"))
                .andExpect(model().attribute("expense", record))
                .andExpect(model().attributeExists("expenseCategories"));
    }
    @Test
    void editExpenseRecord_ShouldUpdateAndRedirect() throws Exception {
        mockMvc.perform(post("/account/expense/edit/1")
                        .param("category", ExpenseCategory.FOOD.name())
                        .param("amount", "15.0")
                        .param("date", "2025-03-15")
                        .param("description", "updated expense"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/expense"));

        verify(expenseRecordService).updateRecord(1,
                ExpenseCategory.FOOD, 15.0,
                LocalDate.of(2025,3,15),"updated expense");
    }


}
