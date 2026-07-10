package com.TaskManager.service;

import com.TaskManager.model.Task;
import com.TaskManager.storage.TaskStorage;

import java.util.List;
import java.util.stream.Collectors;

public class TaskManager {
    private final List<Task> tasks;
    private final TaskStorage taskStorage;

    public TaskManager() {
        this.taskStorage = new TaskStorage();
        this.tasks = taskStorage.loadTasks(); // Load saved tasks on startup
    }

    public List<Task> getAllTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
        taskStorage.saveTasks(tasks); // Save changes
    }

    public void deleteTask(Task task) {
        tasks.remove(task);
        taskStorage.saveTasks(tasks); // Save changes
    }

    public void toggleTaskCompletion(Task task) {
        task.setCompleted(!task.isCompleted());
        taskStorage.saveTasks(tasks); // Save changes
    }

    // Filter tasks by status
    public List<Task> getTasksByCompletion(boolean completed) {
        return tasks.stream()
                .filter(task -> task.isCompleted() == completed)
                .collect(Collectors.toList());
    }
}