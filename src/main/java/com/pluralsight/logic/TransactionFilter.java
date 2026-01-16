package com.pluralsight.logic;

import com.pluralsight.core.Transaction;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionFilter {
    public List<Transaction> byDateRange(List<Transaction> list, LocalDate start, LocalDate end) {
        return list.stream()
                .filter(t -> (start == null || !t.getDate().isBefore(start)) &&
                        (end == null || !t.getDate().isAfter(end)))
                .collect(Collectors.toList());
    }

    public static List<Transaction> byVendor(List<Transaction> list, String vendor) {
        return list.stream()
                .filter(t -> t.getVendor().equalsIgnoreCase(vendor))
                .collect(Collectors.toList());
    }

    public static List<Transaction> depositsOnly(List<Transaction> list) {
        return list.stream().filter(t -> t.getAmount() > 0).collect(Collectors.toList());
    }

    public static List<Transaction> paymentsOnly(List<Transaction> list) {
        return list.stream().filter(t -> t.getAmount() < 0).collect(Collectors.toList());
    }
    // Inside com.pluralsight.logic.TransactionFilter
    public static List<Transaction> customSearch(List<Transaction> list,
                                                 LocalDate start, LocalDate end,
                                                 String desc, String vendor, Double amount) {
        return list.stream()
                .filter(t -> (start == null || !t.getDate().isBefore(start)))
                .filter(t -> (end == null || !t.getDate().isAfter(end)))
                .filter(t -> (desc.isEmpty() || t.getDescription().equalsIgnoreCase(desc)))
                .filter(t -> (vendor.isEmpty() || t.getVendor().equalsIgnoreCase(vendor)))
                .filter(t -> (amount == null || t.getAmount() == amount))
                .collect(Collectors.toList());
    }
}