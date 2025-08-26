package org.gouenji.financeapp.controller.secured;

import org.gouenji.financeapp.dto.ExpenseRecordsContainer;
import org.gouenji.financeapp.dto.IncomeRecordsContainer;
import org.gouenji.financeapp.entity.IncomeRecord;
import org.gouenji.financeapp.entity.enumsRecords.IncomeCategory;
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
        expenseRecordsContainer = expenseRecordService.findAll();
        model.addAttribute("totalIncome", incomeRecordsContainer.getTotal());
        model.addAttribute("totalExpense", expenseRecordsContainer.getTotal());
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
    public String addRecord(@RequestParam IncomeCategory category,
                            @RequestParam double amount,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                            @RequestParam(required = false) String description) {
        incomeRecordService.saveRecord(category, amount, date, description);
        return "redirect:/account/income";
    }

    @GetMapping("/expense")
    public String getExpensePage(Model model) {
        expenseRecordsContainer = expenseRecordService.findAll();
        model.addAttribute("totalExpense", expenseRecordsContainer.getTotal());
        return "private/expense-page";
    }
}


//
////    @PostMapping("/make-record-done")
////    public String makeRecordDone(@RequestParam int id) {
////        recordService.updateRecordStatus(id, RecordStatus.DONE);
////        return "redirect:/account";
////    }
//
//    @PostMapping("/delete-record")
//    public String deleteRecord(@RequestParam int id) {
//        recordService.deleteRecord(id);
//        return "redirect:/account";
//    }
//}
