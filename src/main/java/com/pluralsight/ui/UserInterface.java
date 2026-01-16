package com.pluralsight.ui;

import com.pluralsight.core.Transaction;
import com.pluralsight.data.TransactionRepository;
import com.pluralsight.logic.ReportService;

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

    private void ledgerMenu() {
        List<Transaction> all = repo.loadAll();
        displayTable(all);
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
}