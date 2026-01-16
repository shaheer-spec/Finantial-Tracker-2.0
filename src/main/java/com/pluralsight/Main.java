package com.pluralsight;

import com.pluralsight.ui.gui.FinancialTrackerFrame;
import com.pluralsight.ui.UserInterface;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
//        new UserInterface().start();
        SwingUtilities.invokeLater(() -> new FinancialTrackerFrame().setVisible(true));
    }

}