package com.pluralsight.logic;

import com.pluralsight.core.Transaction;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class ReportService {
    private final TransactionFilter filter = new TransactionFilter();

    public List<Transaction> getMonthToDate(List<Transaction> all) {
        return filter.byDateRange(all, LocalDate.now().withDayOfMonth(1), LocalDate.now());
    }

    public List<Transaction> getPreviousMonth(List<Transaction> all) {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        return filter.byDateRange(all, lastMonth.withDayOfMonth(1), lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()));
    }

    public List<Transaction> getYearToDate(List<Transaction> all) {
        return filter.byDateRange(all, LocalDate.now().withDayOfYear(1), LocalDate.now());
    }

    public void sortByDateDescending(List<Transaction> list) {
        list.sort(Comparator.comparing(Transaction::getDate)
                .thenComparing(Transaction::getTime)
                .reversed());
    }
}