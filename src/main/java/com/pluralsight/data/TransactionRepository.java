package com.pluralsight.data;

import com.pluralsight.core.Transaction;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
    private final String fileName;

    public TransactionRepository(String fileName) {
        this.fileName = fileName;
    }

    public List<Transaction> loadAll() {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                transactions.add(new Transaction(
                        LocalDate.parse(parts[0]),
                        LocalTime.parse(parts[1]),
                        parts[2], parts[3],
                        Double.parseDouble(parts[4])
                ));
            }
        } catch (IOException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
        return transactions;
    }

    public void save(Transaction t) {
        // Standard practice is to use try-with-resources for automatic closing
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            bw.write(t.toString());
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error saving to file: " + e.getMessage());
        }
    }
}