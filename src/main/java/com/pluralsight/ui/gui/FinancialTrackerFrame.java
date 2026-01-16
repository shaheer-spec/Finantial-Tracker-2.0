package com.pluralsight.ui.gui;

import com.pluralsight.core.Transaction;
import com.pluralsight.data.TransactionRepository;
import com.pluralsight.logic.ReportService;
import com.pluralsight.logic.TransactionFilter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class FinancialTrackerFrame extends JFrame {

    private final TransactionRepository repo = new TransactionRepository("transactions.csv");
    private final ReportService reportService = new ReportService();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Date", "Time", "Description", "Vendor", "Amount"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };

    private final JTable ledgerTable = new JTable(tableModel);
    private final JTable reportsTable = new JTable(tableModel);

    private final JTextField dateField = new JTextField(10);
    private final JTextField timeField = new JTextField(10);
    private final JTextField descField = new JTextField(20);
    private final JTextField vendorField = new JTextField(15);
    private final JTextField amountField = new JTextField(10);

    private final JRadioButton depositRadio = new JRadioButton("Deposit", true);
    private final JRadioButton paymentRadio = new JRadioButton("Payment");

    private final JTextField vendorSearchField = new JTextField(15);

    private final JLabel totalLabel = new JLabel("Total: 0.00");

    public FinancialTrackerFrame() {
        super("Financial Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Add Transaction", buildAddPanel());
        tabs.addTab("Ledger", buildLedgerPanel());
        tabs.addTab("Reports", buildReportsPanel());

        add(tabs, BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        refreshTable(repo.loadAll());
        updateTotalFromFile();
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 14f));
        bar.add(totalLabel);
        return bar;
    }

    private JPanel buildAddPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        ButtonGroup group = new ButtonGroup();
        group.add(depositRadio);
        group.add(paymentRadio);

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Type:"), gbc);

        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        typePanel.add(depositRadio);
        typePanel.add(paymentRadio);

        gbc.gridx = 1;
        gbc.gridy = row;
        form.add(typePanel, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Date (yyyy-MM-dd):"), gbc);

        dateField.setText(LocalDate.now().toString());
        gbc.gridx = 1;
        gbc.gridy = row;
        form.add(dateField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Time (HH:mm:ss):"), gbc);

        timeField.setText(LocalTime.now().withNano(0).toString());
        gbc.gridx = 1;
        gbc.gridy = row;
        form.add(timeField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        form.add(descField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Vendor:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        form.add(vendorField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        form.add(amountField, gbc);

        JButton saveBtn = new JButton("Save Transaction");
        saveBtn.addActionListener(e -> saveTransactionFromForm());

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> clearForm());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.add(saveBtn);
        buttons.add(clearBtn);

        panel.add(form, BorderLayout.NORTH);
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildLedgerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton allBtn = new JButton("All");
        allBtn.addActionListener(e -> refreshTable(repo.loadAll()));

        JButton depositsBtn = new JButton("Deposits");
        depositsBtn.addActionListener(e -> {
            List<Transaction> all = repo.loadAll();
            refreshTable(TransactionFilter.depositsOnly(all));
        });

        JButton paymentsBtn = new JButton("Payments");
        paymentsBtn.addActionListener(e -> {
            List<Transaction> all = repo.loadAll();
            refreshTable(TransactionFilter.paymentsOnly(all));
        });

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable(repo.loadAll()));

        top.add(allBtn);
        top.add(depositsBtn);
        top.add(paymentsBtn);
        top.add(refreshBtn);

        ledgerTable.setFillsViewportHeight(true);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(ledgerTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton mtdBtn = new JButton("MTD");
        mtdBtn.addActionListener(e -> {
            List<Transaction> all = repo.loadAll();
            refreshTable(reportService.getMonthToDate(all));
        });

        JButton prevMonthBtn = new JButton("Previous Month");
        prevMonthBtn.addActionListener(e -> {
            List<Transaction> all = repo.loadAll();
            refreshTable(reportService.getPreviousMonth(all));
        });

        JButton ytdBtn = new JButton("YTD");
        ytdBtn.addActionListener(e -> {
            List<Transaction> all = repo.loadAll();
            refreshTable(reportService.getYearToDate(all));
        });

        JButton prevYearBtn = new JButton("Previous Year");
        prevYearBtn.addActionListener(e -> {
            List<Transaction> all = repo.loadAll();
            refreshTable(reportService.getPreviousYear(all));
        });

        top.add(mtdBtn);
        top.add(prevMonthBtn);
        top.add(ytdBtn);
        top.add(prevYearBtn);

        JPanel vendorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        vendorPanel.add(new JLabel("Vendor:"));
        vendorPanel.add(vendorSearchField);

        JButton vendorBtn = new JButton("Search Vendor");
        vendorBtn.addActionListener(e -> {
            List<Transaction> all = repo.loadAll();
            String vendor = vendorSearchField.getText().trim();
            if (vendor.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a vendor name.", "Missing Vendor", JOptionPane.WARNING_MESSAGE);
                return;
            }
            refreshTable(TransactionFilter.byVendor(all, vendor));
        });

        JButton customBtn = new JButton("Custom Search...");
        customBtn.addActionListener(e -> openCustomSearchDialog());

        JButton showAllBtn = new JButton("Show All");
        showAllBtn.addActionListener(e -> refreshTable(repo.loadAll()));

        vendorPanel.add(vendorBtn);
        vendorPanel.add(customBtn);
        vendorPanel.add(showAllBtn);

        reportsTable.setFillsViewportHeight(true);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportsTable), BorderLayout.CENTER);
        panel.add(vendorPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void saveTransactionFromForm() {
        try {
            boolean isDeposit = depositRadio.isSelected();

            LocalDate date = LocalDate.parse(dateField.getText().trim());
            LocalTime time = LocalTime.parse(timeField.getText().trim());

            String desc = descField.getText().trim();
            String vendor = vendorField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());

            if (desc.isEmpty() || vendor.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Description and Vendor are required.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!isDeposit && amount > 0) amount *= -1;
            if (isDeposit && amount < 0) amount *= -1;

            Transaction t = new Transaction(date, time, desc, vendor, amount);
            repo.save(t);

            JOptionPane.showMessageDialog(this, "Transaction saved!", "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            refreshTable(repo.loadAll());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid input:\n" + ex.getMessage() +
                            "\n\nExamples:\nDate: 2026-01-16\nTime: 14:30:00\nAmount: 25.50",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        descField.setText("");
        vendorField.setText("");
        amountField.setText("");
        dateField.setText(LocalDate.now().toString());
        timeField.setText(LocalTime.now().withNano(0).toString());
        depositRadio.setSelected(true);
    }

    private void refreshTable(List<Transaction> list) {
        reportService.sortByDateDescending(list);

        tableModel.setRowCount(0);
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                    t.getDate().toString(),
                    t.getTime().toString(),
                    t.getDescription(),
                    t.getVendor(),
                    String.format("%.2f", t.getAmount())
            });
        }

        updateTotalFromFile();
    }

    private void updateTotalFromFile() {
        List<Transaction> all = repo.loadAll();
        double total = 0.0;

        for (Transaction t : all) {
            total += t.getAmount();
        }

        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private void openCustomSearchDialog() {
        JDialog dialog = new JDialog(this, "Custom Search", true);
        dialog.setSize(450, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JTextField startDateField = new JTextField();
        JTextField endDateField = new JTextField();
        JTextField desc = new JTextField();
        JTextField vendor = new JTextField();
        JTextField amount = new JTextField();

        form.add(new JLabel("Start Date (yyyy-MM-dd):"));
        form.add(startDateField);

        form.add(new JLabel("End Date (yyyy-MM-dd):"));
        form.add(endDateField);

        form.add(new JLabel("Description (exact):"));
        form.add(desc);

        form.add(new JLabel("Vendor (exact):"));
        form.add(vendor);

        form.add(new JLabel("Amount (exact):"));
        form.add(amount);

        form.add(new JLabel("Leave any field blank to skip it."));
        form.add(new JLabel(""));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton searchBtn = new JButton("Search");
        JButton cancelBtn = new JButton("Cancel");

        cancelBtn.addActionListener(e -> dialog.dispose());
        searchBtn.addActionListener(e -> {
            try {
                String startStr = startDateField.getText().trim();
                String endStr = endDateField.getText().trim();
                LocalDate start = startStr.isEmpty() ? null : LocalDate.parse(startStr);
                LocalDate end = endStr.isEmpty() ? null : LocalDate.parse(endStr);

                String d = desc.getText().trim();
                String v = vendor.getText().trim();

                String amtStr = amount.getText().trim();
                Double amt = amtStr.isEmpty() ? null : Double.parseDouble(amtStr);

                List<Transaction> all = repo.loadAll();
                List<Transaction> results = TransactionFilter.customSearch(all, start, end, d, v, amt);

                refreshTable(results);
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Invalid input:\n" + ex.getMessage(),
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        buttons.add(searchBtn);
        buttons.add(cancelBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
