package com.TaskManager.ui;

import com.TaskManager.model.Priority;
import com.TaskManager.model.Task;
import com.TaskManager.service.TaskManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MainFrame extends JFrame {
    private final TaskManager taskManager;
    private JTable taskTable;
    private DefaultTableModel tableModel;

    // Input fields
    private JTextField titleField;
    private JTextField descField;
    private JTextField dateField;
    private JComboBox<Priority> priorityCombo;
    private JComboBox<String> filterCombo;

    public MainFrame() {
        this.taskManager = new TaskManager();
        initializeUI();
        refreshTable();
    }

    private void initializeUI() {
        setTitle("Desktop Task Manager");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- LEFT SIDE: Input Form Panel ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Task"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Input
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        titleField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(titleField, gbc);

        // Description Input
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);
        descField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(descField, gbc);

        // Due Date Input
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Due Date (YYYY-MM-DD):"), gbc);
        dateField = new JTextField(LocalDate.now().toString(), 15);
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);

        // Priority Dropdown
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Priority:"), gbc);
        priorityCombo = new JComboBox<>(Priority.values());
        gbc.gridx = 1;
        formPanel.add(priorityCombo, gbc);

        // Add Button
        JButton addButton = new JButton("Add Task");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        addButton.addActionListener(e -> handleAddTask());
        formPanel.add(addButton, gbc);

        add(formPanel, BorderLayout.WEST);

        // --- CENTER: Task List Display (Table) ---
        String[] columnNames = {"Status", "Title", "Description", "Due Date", "Priority"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table cells are read-only
            }
        };
        taskTable = new JTable(tableModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(taskTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- SOUTH: Control Bar (Action Buttons & Filter) ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        // Filter Dropdown
        controlPanel.add(new JLabel("Filter:"));
        filterCombo = new JComboBox<>(new String[]{"All Tasks", "Active Tasks", "Completed Tasks"});
        filterCombo.addActionListener(e -> refreshTable());
        controlPanel.add(filterCombo);

        // Action Buttons
        JButton completeBtn = new JButton("Toggle Complete");
        completeBtn.addActionListener(e -> handleToggleComplete());
        controlPanel.add(completeBtn);

        JButton deleteBtn = new JButton("Delete Task");
        deleteBtn.addActionListener(e -> handleDeleteTask());
        controlPanel.add(deleteBtn);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        tableModel.setRowCount(0); // Clear current table content

        String selectedFilter = (String) filterCombo.getSelectedItem();
        List<Task> displayedTasks;

        if ("Active Tasks".equals(selectedFilter)) {
            displayedTasks = taskManager.getTasksByCompletion(false);
        } else if ("Completed Tasks".equals(selectedFilter)) {
            displayedTasks = taskManager.getTasksByCompletion(true);
        } else {
            displayedTasks = taskManager.getAllTasks();
        }

        for (Task task : displayedTasks) {
            Object[] row = {
                    task.isCompleted() ? "✓ Completed" : "⏳ Pending",
                    task.getTitle(),
                    task.getDescription(),
                    task.getDueDate().toString(),
                    task.getPriority()
            };
            tableModel.addRow(row);
        }
    }

    private void handleAddTask() {
        String title = titleField.getText().trim();
        String desc = descField.getText().trim();
        String dateStr = dateField.getText().trim();
        Priority priority = (Priority) priorityCombo.getSelectedItem();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Task Title cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate dueDate = LocalDate.parse(dateStr);
            Task newTask = new Task(title, desc, dueDate, priority);
            taskManager.addTask(newTask);

            // Reset Input Form
            titleField.setText("");
            descField.setText("");
            dateField.setText(LocalDate.now().toString());
            priorityCombo.setSelectedIndex(0);

            refreshTable();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Date format. Use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleToggleComplete() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task from the list.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Task selectedTask = getSelectedTaskFromRow(selectedRow);
        if (selectedTask != null) {
            taskManager.toggleTaskCompletion(selectedTask);
            refreshTable();
        }
    }

    private void handleDeleteTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Task selectedTask = getSelectedTaskFromRow(selectedRow);
        if (selectedTask != null) {
            taskManager.deleteTask(selectedTask);
            refreshTable();
        }
    }

    private Task getSelectedTaskFromRow(int row) {
        String selectedFilter = (String) filterCombo.getSelectedItem();
        List<Task> currentTasks;

        if ("Active Tasks".equals(selectedFilter)) {
            currentTasks = taskManager.getTasksByCompletion(false);
        } else if ("Completed Tasks".equals(selectedFilter)) {
            currentTasks = taskManager.getTasksByCompletion(true);
        } else {
            currentTasks = taskManager.getAllTasks();
        }

        if (row >= 0 && row < currentTasks.size()) {
            return currentTasks.get(row);
        }
        return null;
    }
}