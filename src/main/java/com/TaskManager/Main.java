package com.TaskManager;

import com.TaskManager.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Apply System styling to make the Swing layout match your OS interface
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("System Look and Feel unavailable, using fallback.");
        }

        // Run UI initialization on the Event Dispatch Thread (standard safe practices for Swing)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}