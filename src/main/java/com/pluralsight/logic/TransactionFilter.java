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

    public List<Transaction> byVendor(List<Transaction> list, String vendor) {
        return list.stream()
                .filter(t -> t.getVendor().equalsIgnoreCase(vendor))
                .collect(Collectors.toList());
    }

    public List<Transaction> depositsOnly(List<Transaction> list) {
        return list.stream().filter(t -> t.getAmount() > 0).collect(Collectors.toList());
    }

    public List<Transaction> paymentsOnly(List<Transaction> list) {
        return list.stream().filter(t -> t.getAmount() < 0).collect(Collectors.toList());
    }
}