package org.gouenji.financeapp.controller.secured;

import org.gouenji.financeapp.dto.ExpenseRecordsContainer;
import org.gouenji.financeapp.dto.IncomeRecordsContainer;
import org.gouenji.financeapp.entity.Record;
import org.gouenji.financeapp.entity.enums.records.ExpenseCategory;
import org.gouenji.financeapp.entity.enums.records.IncomeCategory;
import org.gouenji.financeapp.service.records.ExpenseRecordService;
import org.gouenji.financeapp.service.records.IncomeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Controller
@RequestMapping("/account")
public class PrivateAccountController {

    private final IncomeRecordService incomeRecordService;
    private IncomeRecordsContainer incomeRecordsContainer;

    private final ExpenseRecordService expenseRecordService;
    private ExpenseRecordsContainer expenseRecordsContainer;

    @Autowired
    public PrivateAccountController(IncomeRecordService incomeRecordService, ExpenseRecordService expenseRecordService) {
        this.incomeRecordService = incomeRecordService;
        this.expenseRecordService = expenseRecordService;
    }

    @GetMapping
    public String getMainPage(Model model) {
        incomeRecordsContainer = incomeRecordService.findAll(null);
        expenseRecordsContainer = expenseRecordService.findAll(null);
        List<Record> allRecords = Stream.concat(
                        incomeRecordsContainer.getRecords().stream(),
                        expenseRecordsContainer.getRecords().stream()
                )
                .sorted(Comparator.comparing(Record::getDate).reversed())
                .toList();
        model.addAttribute("totalIncome", incomeRecordsContainer.getTotal());
        model.addAttribute("totalExpense", expenseRecordsContainer.getTotal());
        model.addAttribute("totalBalance",
                incomeRecordsContainer.getTotal() - expenseRecordsContainer.getTotal());
        model.addAttribute("monthIncome", incomeRecordsContainer.hasMonthTotal() ?
                incomeRecordsContainer.getMonthTotal() : 0);
        model.addAttribute("monthExpense", expenseRecordsContainer.hasMonthTotal() ?
                expenseRecordsContainer.getMonthTotal() : 0);
        model.addAttribute("recentTransactionsCount", allRecords.size());
        model.addAttribute("recentTransactions", allRecords);
        return "private/account-page";
    }

    @GetMapping("/income")
    public String getIncomePage(Model model, @RequestParam(required = false) String category) {
        incomeRecordsContainer = incomeRecordService.findAll(category);
        model.addAttribute("incomeRecords", incomeRecordsContainer.getRecords());
        model.addAttribute("totalIncome", incomeRecordsContainer.getTotal());
        model.addAttribute("filteredTotalIncome", incomeRecordsContainer.hasFilteredTotal() ?
                incomeRecordsContainer.getFilteredTotal() : incomeRecordsContainer.getTotal());
        model.addAttribute("incomeCategories", IncomeCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("averageIncome", incomeRecordsContainer.hasAverageTotal() ?
                incomeRecordsContainer.getAverageTotal() : 0);
        model.addAttribute("monthTotal", incomeRecordsContainer.hasMonthTotal() ?
                incomeRecordsContainer.getMonthTotal() : 0);
        return "private/income-page";
    }

    @GetMapping("/income/add")
    public String getIncomeAddPage(Model model) {
        model.addAttribute("incomeCategories", IncomeCategory.values());
        return "private/income-add-page";
    }

    @PostMapping("/income/add-record")
    public String addIncomeRecord(@RequestParam IncomeCategory category,
                            @RequestParam double amount,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                            @RequestParam(required = false) String description) {
        incomeRecordService.saveRecord(category, amount, date, description);
        return "redirect:/account/income";
    }

    @GetMapping("/expense")
    public String getExpensePage(Model model, @RequestParam(required = false) String category) {
        expenseRecordsContainer = expenseRecordService.findAll(category);
        model.addAttribute("expenseRecords", expenseRecordsContainer.getRecords());
        model.addAttribute("totalExpense", expenseRecordsContainer.getTotal());
        model.addAttribute("filteredTotalExpense", expenseRecordsContainer.hasFilteredTotal() ?
                expenseRecordsContainer.getFilteredTotal() : expenseRecordsContainer.getTotal());
        model.addAttribute("expenseCategories", ExpenseCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("averageExpense", expenseRecordsContainer.hasAverageTotal() ?
                expenseRecordsContainer.getAverageTotal() : 0);
        model.addAttribute("monthTotal", expenseRecordsContainer.hasMonthTotal() ?
                expenseRecordsContainer.getMonthTotal() : 0);
        return "private/expense-page";
    }

    @GetMapping("/expense/add")
    public String getExpenseAddPage(Model model) {
        model.addAttribute("expenseCategories", ExpenseCategory.values());
        return "private/expense-add-page";
    }

    @PostMapping("/expense/add-record")
    public String addExpenseRecord(@RequestParam ExpenseCategory category,
                            @RequestParam double amount,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                            @RequestParam(required = false) String description) {
        expenseRecordService.saveRecord(category, amount, date, description);
        return "redirect:/account/expense";
    }
}

//
//    @PostMapping("/delete-record")
//    public String deleteRecord(@RequestParam int id) {
//        recordService.deleteRecord(id);
//        return "redirect:/account";
//    }
//}
