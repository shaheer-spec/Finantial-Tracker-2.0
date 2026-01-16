package com.pluralsight.ui;

import com.pluralsight.core.Transaction;
import com.pluralsight.data.TransactionRepository;
import com.pluralsight.logic.ReportService;
import com.pluralsight.logic.TransactionFilter;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class UserInterface {
    private final TransactionRepository repo = new TransactionRepository("transactions.csv");
    private final ReportService reportService = new ReportService();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Financial Tracker ---");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String choice = scanner.nextLine().toUpperCase();
            switch (choice) {
                case "D" -> handleTransaction(true);
                case "P" -> handleTransaction(false);
                case "L" -> ledgerMenu();
                case "X" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void displayTable(List<Transaction> list) {
        reportService.sortByDateDescending(list);
        System.out.printf("%-12s %-10s %-30s %-20s %10s\n", "Date", "Time", "Desc", "Vendor", "Amount");
        for (Transaction t : list) {
            System.out.printf("%-12s %-10s %-30s %-20s %10.2f\n",
                    t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
        }
    }

    // Inside com.pluralsight.ui.UserInterface
    private void handleTransaction(boolean isDeposit) {
        System.out.println(isDeposit ? "\n--- Add Deposit ---" : "\n--- Add Payment ---");

        // In a real app, you might use LocalDate.now(),
        // but here we keep your manual input requirement:
        System.out.print("Date (yyyy-MM-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());

        System.out.print("Time (HH:mm:ss): ");
        LocalTime time = LocalTime.parse(scanner.nextLine());

        System.out.print("Description: ");
        String desc = scanner.nextLine();

        System.out.print("Vendor: ");
        String vendor = scanner.nextLine();

        System.out.print("Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());

        // Business Logic: Force payments to be negative and deposits to be positive
        if (!isDeposit && amount > 0) amount *= -1;
        if (isDeposit && amount < 0) amount *= -1;

        Transaction t = new Transaction(date, time, desc, vendor, amount);
        repo.save(t);
        System.out.println("Transaction recorded successfully!");
    }
    // Inside com.pluralsight.ui.UserInterface
    private void handleCustomSearch() {
        System.out.println("\n--- Custom Search (Leave blank to skip) ---");

        System.out.print("Start Date (yyyy-MM-dd): ");
        String startStr = scanner.nextLine();
        LocalDate start = startStr.isEmpty() ? null : LocalDate.parse(startStr);

        System.out.print("End Date (yyyy-MM-dd): ");
        String endStr = scanner.nextLine();
        LocalDate end = endStr.isEmpty() ? null : LocalDate.parse(endStr);

        System.out.print("Description: ");
        String desc = scanner.nextLine();

        System.out.print("Vendor: ");
        String vendor = scanner.nextLine();

        System.out.print("Amount: ");
        String amountStr = scanner.nextLine();
        Double amount = amountStr.isEmpty() ? null : Double.parseDouble(amountStr);

        // Get data, TransactionFilter it, and display
        List<Transaction> all = repo.loadAll();
        List<Transaction> results = TransactionFilter.customSearch(all, start, end, desc, vendor, amount);

        displayTable(results);
    }
    // Inside com.pluralsight.ui.UserInterface

    private void ledgerMenu() {
        boolean inLedger = true;
        while (inLedger) {
            System.out.println("\n--- Ledger Menu ---");
            System.out.println("A) All | D) Deposits | P) Payments | R) Reports | H) Home");
            String choice = scanner.nextLine().toUpperCase();

            // Load fresh data for each selection to ensure accuracy
            List<Transaction> allTransactions = repo.loadAll();

            switch (choice) {
                case "A" -> displayTable(allTransactions);
                case "D" -> displayTable(TransactionFilter.depositsOnly(allTransactions));
                case "P" -> displayTable(TransactionFilter.paymentsOnly(allTransactions));
                case "R" -> reportsMenu(allTransactions); // Pass data to the next menu
                case "H" -> inLedger = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }
    // Inside com.pluralsight.ui.UserInterface

    private void reportsMenu(List<Transaction> data) {
        boolean inReports = true;
        while (inReports) {
            System.out.println("\n--- Reports ---");
            System.out.println("1) MTD | 2) Previous Month | 3) YTD | 4) Previous Year | 5) Search Vendor | 6) Custom | 0) Back");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> displayTable(reportService.getMonthToDate(data));
                case "2" -> displayTable(reportService.getPreviousMonth(data));
                case "3" -> displayTable(reportService.getYearToDate(data));
                case "4" -> displayTable(reportService.getPreviousYear(data)); // Logic in ReportService
                case "5" -> {
                    System.out.print("Enter Vendor: ");
                    String v = scanner.nextLine();
                    displayTable(TransactionFilter.byVendor(data, v));
                }
                case "6" -> handleCustomSearch(); // Calls the multi-TransactionFilter logic
                case "0" -> inReports = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }
}